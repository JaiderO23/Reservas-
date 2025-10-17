package service;

import exception.BadRequestException;
import exception.ResourceNotFoundException;
import model.DiaBloqueado;
import repository.DiaBloqueadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaBloqueadoService {

    private final DiaBloqueadoRepository diaBloqueadoRepository;

    @Transactional(readOnly = true)
    public List<DiaBloqueado> obtenerTodos() {
        return diaBloqueadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DiaBloqueado> obtenerFuturos() {
        return diaBloqueadoRepository.findByFechaAfter(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public DiaBloqueado obtenerPorId(Long id) {
        return diaBloqueadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Día bloqueado no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public boolean estaBloqueda(LocalDate fecha) {
        return diaBloqueadoRepository.existsByFecha(fecha);
    }

    @Transactional
    public DiaBloqueado crear(DiaBloqueado diaBloqueado) {
        if (diaBloqueadoRepository.existsByFecha(diaBloqueado.getFecha())) {
            throw new BadRequestException("Ya existe un bloqueo para esa fecha");
        }
        return diaBloqueadoRepository.save(diaBloqueado);
    }

    @Transactional
    public DiaBloqueado actualizar(Long id, DiaBloqueado diaBloqueadoActualizado) {
        DiaBloqueado diaBloqueado = obtenerPorId(id);
        diaBloqueado.setFecha(diaBloqueadoActualizado.getFecha());
        diaBloqueado.setMotivo(diaBloqueadoActualizado.getMotivo());
        return diaBloqueadoRepository.save(diaBloqueado);
    }

    @Transactional
    public void eliminar(Long id) {
        DiaBloqueado diaBloqueado = obtenerPorId(id);
        diaBloqueadoRepository.delete(diaBloqueado);
    }
}