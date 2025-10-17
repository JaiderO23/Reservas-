package service;


import exception.BadRequestException;
import exception.ResourceNotFoundException;
import model.Servicio;
import repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Servicio> obtenerActivos() {
        return servicioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Servicio obtenerPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Transactional
    public Servicio crear(Servicio servicio) {
        if (servicioRepository.existsByNombre(servicio.getNombre())) {
            throw new BadRequestException("Ya existe un servicio con ese nombre");
        }
        return servicioRepository.save(servicio);
    }

    @Transactional
    public Servicio actualizar(Long id, Servicio servicioActualizado) {
        Servicio servicio = obtenerPorId(id);
        servicio.setNombre(servicioActualizado.getNombre());
        servicio.setDescripcion(servicioActualizado.getDescripcion());
        servicio.setDuracionMinutos(servicioActualizado.getDuracionMinutos());
        servicio.setPrecio(servicioActualizado.getPrecio());
        servicio.setActivo(servicioActualizado.getActivo());
        return servicioRepository.save(servicio);
    }

    @Transactional
    public void eliminar(Long id) {
        Servicio servicio = obtenerPorId(id);
        servicioRepository.delete(servicio);
    }
}