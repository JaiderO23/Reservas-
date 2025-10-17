package service;


import exception.ResourceNotFoundException;
import model.Cliente;
import repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElse(null);
    }

    @Transactional
    public Cliente crear(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        Cliente cliente = obtenerPorId(id);
        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setEmail(clienteActualizado.getEmail());
        cliente.setTelefono(clienteActualizado.getTelefono());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = obtenerPorId(id);
        clienteRepository.delete(cliente);
    }

    @Transactional
    public Cliente crearOActualizar(String nombre, String email, String telefono) {
        Cliente clienteExistente = obtenerPorEmail(email);
        if (clienteExistente != null) {
            clienteExistente.setNombre(nombre);
            clienteExistente.setTelefono(telefono);
            return clienteRepository.save(clienteExistente);
        }

        Cliente nuevoCliente = Cliente.builder()
                .nombre(nombre)
                .email(email)
                .telefono(telefono)
                .build();
        return clienteRepository.save(nuevoCliente);
    }
}