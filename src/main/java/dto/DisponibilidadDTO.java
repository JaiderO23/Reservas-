package dto;

import lombok.*;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadDTO {
    private boolean disponible;
    private String motivo;
    private List<HorarioDisponibleDTO> horariosDisponibles;
}