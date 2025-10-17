package controller;

import model.HorarioTrabajo;
import service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioTrabajoController {

    private final HorarioTrabajoService horarioTrabajoService;

    @GetMapping
    public ResponseEntity<List<HorarioTrabajo>> obtenerTodos() {
        return ResponseEntity.ok(horarioTrabajoService.obtenerTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<HorarioTrabajo>> obtenerActivos() {
        return ResponseEntity.ok(horarioTrabajoService.obtenerActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabajo> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioTrabajoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<HorarioTrabajo> crear(@RequestBody HorarioTrabajo horario) {
        HorarioTrabajo creado = horarioTrabajoService.crear(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioTrabajo> actualizar(@PathVariable Long id, @RequestBody HorarioTrabajo horario) {
        return ResponseEntity.ok(horarioTrabajoService.actualizar(id, horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioTrabajoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}