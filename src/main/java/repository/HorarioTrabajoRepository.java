package repository;

import model.HorarioTrabajo;
import model.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioTrabajoRepository extends JpaRepository<HorarioTrabajo, Long> {

    Optional<HorarioTrabajo> findByDiaSemana(DiaSemana diaSemana);

    List<HorarioTrabajo> findByActivoTrue();
}