package controller;

import model.DiaBloqueado;
import service.DiaBloqueadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dias-bloqueados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiaBloqueadoController {

    private final DiaBloqueadoService diaBloqueadoService;

    @GetMapping
    public ResponseEntity<List<DiaBloqueado>> obtenerTodos() {
        return ResponseEntity.ok(diaBloqueadoService.obtenerTodos());
    }

    @GetMapping("/futuros")
    public ResponseEntity<List<DiaBloqueado>> obtenerFuturos() {
        return ResponseEntity.ok(diaBloqueadoService.obtenerFuturos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaBloqueado> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diaBloqueadoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DiaBloqueado> crear(@RequestBody DiaBloqueado diaBloqueado) {
        DiaBloqueado creado = diaBloqueadoService.crear(diaBloqueado);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaBloqueado> actualizar(@PathVariable Long id, @RequestBody DiaBloqueado diaBloqueado) {
        return ResponseEntity.ok(diaBloqueadoService.actualizar(id, diaBloqueado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        diaBloqueadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}