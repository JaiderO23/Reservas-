package repository;

import model.Reserva;
import model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByCodigoConfirmacion(String codigoConfirmacion);

    List<Reserva> findByFechaReserva(LocalDate fecha);

    List<Reserva> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByEstadoOrderByFechaReservaAscHoraInicioAsc(EstadoReserva estado);

    @Query("SELECT r FROM Reserva r WHERE r.fechaReserva = :fecha " +
            "AND r.estado NOT IN ('CANCELADA') " +
            "AND ((r.horaInicio <= :horaInicio AND r.horaFin > :horaInicio) " +
            "OR (r.horaInicio < :horaFin AND r.horaFin >= :horaFin) " +
            "OR (r.horaInicio >= :horaInicio AND r.horaFin <= :horaFin))")
    List<Reserva> findConflictingReservas(
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}