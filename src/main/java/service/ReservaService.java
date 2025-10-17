package service;

import dto.DisponibilidadDTO;
import dto.HorarioDisponibleDTO;
import exception.BadRequestException;
import model.*;
import model.enums.DiaSemana;
import model.enums.EstadoReserva;
import repository.*;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.CodigoGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ServicioService servicioService;
    private final HorarioTrabajoService horarioTrabajoService;  // ← AGREGAR ESTO
    private final ClienteService clienteService;
    private final DiaBloqueadoService diaBloqueadoService;

    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorEstado(EstadoReserva estado) {
        return reservaRepository.findByEstadoOrderByFechaReservaAscHoraInicioAsc(estado);
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorFecha(LocalDate fecha) {
        return reservaRepository.findByFechaReserva(fecha);
    }

    @Transactional(readOnly = true)
    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Reserva obtenerPorCodigo(String codigo) {
        return reservaRepository.findByCodigoConfirmacion(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con código: " + codigo));
    }

    @Transactional
    public Reserva crear(Long servicioId, LocalDate fecha, LocalTime horaInicio,
                         String nombreCliente, String emailCliente, String telefonoCliente, String notas) {

        // Obtener servicio
        Servicio servicio = servicioService.obtenerPorId(servicioId);

        // Calcular hora de fin
        LocalTime horaFin = horaInicio.plusMinutes(servicio.getDuracionMinutos());

        // Verificar conflictos
        List<Reserva> conflictos = reservaRepository.findConflictingReservas(fecha, horaInicio, horaFin);
        if (!conflictos.isEmpty()) {
            throw new BadRequestException("Ya existe una reserva en ese horario");
        }

        // Crear o actualizar cliente
        Cliente cliente = clienteService.crearOActualizar(nombreCliente, emailCliente, telefonoCliente);

        // Generar código de confirmación
        String codigo = CodigoGenerator.generarCodigo();

        // Crear reserva
        Reserva reserva = Reserva.builder()
                .cliente(cliente)
                .servicio(servicio)
                .fechaReserva(fecha)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .estado(EstadoReserva.PENDIENTE)
                .notas(notas)
                .codigoConfirmacion(codigo)
                .build();

        return reservaRepository.save(reserva);
    }


    @Transactional
    public Reserva actualizarEstado(Long id, EstadoReserva estado) {
        Reserva reserva = obtenerPorId(id);
        reserva.setEstado(estado);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizar(Long id, Reserva reservaActualizada) {
        Reserva reserva = obtenerPorId(id);
        reserva.setFechaReserva(reservaActualizada.getFechaReserva());
        reserva.setHoraInicio(reservaActualizada.getHoraInicio());
        reserva.setHoraFin(reservaActualizada.getHoraFin());
        reserva.setEstado(reservaActualizada.getEstado());
        reserva.setNotas(reservaActualizada.getNotas());
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminar(Long id) {
        Reserva reserva = obtenerPorId(id);
        reservaRepository.delete(reserva);
    }

    public DisponibilidadDTO obtenerDisponibilidad(LocalDate fecha, Long servicioId) {
        // Obtener el servicio
        Servicio servicio = servicioService.obtenerPorId(servicioId);

        // Convertir el día de la semana a DiaSemana enum
        DiaSemana diaSemana = convertirADiaSemana(fecha.getDayOfWeek());

        // Obtener horarios de trabajo para ese día
        List<HorarioTrabajo> horariosTrabajo = horarioTrabajoService.obtenerActivos()
                .stream()
                .filter(h -> h.getDiaSemana().equals(diaSemana))
                .toList();

        if (horariosTrabajo.isEmpty()) {
            return DisponibilidadDTO.builder()
                    .disponible(false)
                    .motivo("No hay horarios de atención disponibles para este día")
                    .horariosDisponibles(new ArrayList<>())
                    .build();
        }

        // Verificar si el día está bloqueado
        boolean diaBloqueado = diaBloqueadoService.obtenerTodos()
                .stream()
                .anyMatch(d -> d.getFecha().equals(fecha));

        if (diaBloqueado) {
            return DisponibilidadDTO.builder()
                    .disponible(false)
                    .motivo("Este día está bloqueado para reservas")
                    .horariosDisponibles(new ArrayList<>())
                    .build();
        }

        // Obtener reservas existentes
        List<Reserva> reservasExistentes = obtenerPorFecha(fecha)
                .stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.PENDIENTE)
                .toList();

        // Generar horarios disponibles
        List<HorarioDisponibleDTO> horariosDisponibles = new ArrayList<>();
        HorarioTrabajo horario = horariosTrabajo.get(0);

        LocalTime horaActual = horario.getHoraInicio();
        LocalTime horaFin = horario.getHoraFin();
        int duracion = servicio.getDuracionMinutos();

        while (horaActual.plusMinutes(duracion).isBefore(horaFin)
                || horaActual.plusMinutes(duracion).equals(horaFin)) {

            LocalTime horaFinal = horaActual;
            LocalTime horaFinBloque = horaActual.plusMinutes(duracion);

            // Verificar si el horario está disponible
            boolean disponible = reservasExistentes.stream()
                    .noneMatch(r -> {
                        LocalTime reservaInicio = r.getHoraInicio();
                        LocalTime reservaFin = reservaInicio.plusMinutes(r.getServicio().getDuracionMinutos());

                        // Verificar si hay conflicto de horarios
                        return (horaFinal.isBefore(reservaFin) && horaFinBloque.isAfter(reservaInicio));
                    });

            horariosDisponibles.add(HorarioDisponibleDTO.builder()
                    .horaInicio(horaActual)
                    .horaFin(horaFinBloque)
                    .disponible(disponible)
                    .build());

            horaActual = horaActual.plusMinutes(30); // Intervalos de 30 minutos
        }

        return DisponibilidadDTO.builder()
                .disponible(!horariosDisponibles.isEmpty())
                .motivo(horariosDisponibles.isEmpty() ? "No hay horarios disponibles" : null)
                .horariosDisponibles(horariosDisponibles)
                .build();
    }

    private DiaSemana convertirADiaSemana(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
