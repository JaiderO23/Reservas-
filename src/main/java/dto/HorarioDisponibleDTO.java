package dto;

import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDisponibleDTO {
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible;
}