package repository;

import model.DiaBloqueado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaBloqueadoRepository extends JpaRepository<DiaBloqueado, Long> {

    Optional<DiaBloqueado> findByFecha(LocalDate fecha);

    boolean existsByFecha(LocalDate fecha);

    List<DiaBloqueado> findByFechaBetween(LocalDate inicio, LocalDate fin);

    List<DiaBloqueado> findByFechaAfter(LocalDate fecha);
}