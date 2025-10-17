package service;

import exception.ResourceNotFoundException;
import model.HorarioTrabajo;
import model.enums.DiaSemana;
import repository.HorarioTrabajoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dto.DisponibilidadDTO;
import dto.HorarioDisponibleDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioTrabajoService {

    private final HorarioTrabajoRepository horarioTrabajoRepository;

    @Transactional(readOnly = true)
    public List<HorarioTrabajo> obtenerTodos() {
        return horarioTrabajoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<HorarioTrabajo> obtenerActivos() {
        return horarioTrabajoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public HorarioTrabajo obtenerPorId(Long id) {
        return horarioTrabajoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public HorarioTrabajo obtenerPorDia(DiaSemana dia) {
        return horarioTrabajoRepository.findByDiaSemana(dia)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado para: " + dia));
    }

    @Transactional
    public HorarioTrabajo crear(HorarioTrabajo horario) {
        return horarioTrabajoRepository.save(horario);
    }

    @Transactional
    public HorarioTrabajo actualizar(Long id, HorarioTrabajo horarioActualizado) {
        HorarioTrabajo horario = obtenerPorId(id);
        horario.setDiaSemana(horarioActualizado.getDiaSemana());
        horario.setHoraInicio(horarioActualizado.getHoraInicio());
        horario.setHoraFin(horarioActualizado.getHoraFin());
        horario.setActivo(horarioActualizado.getActivo());
        return horarioTrabajoRepository.save(horario);
    }

    @Transactional
    public void eliminar(Long id) {
        HorarioTrabajo horario = obtenerPorId(id);
        horarioTrabajoRepository.delete(horario);
    }
}