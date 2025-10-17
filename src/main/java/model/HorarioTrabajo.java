package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import model.enums.DiaSemana;

import java.time.LocalTime;

@Entity
@Table(name = "horarios_trabajo", uniqueConstraints = {
        @UniqueConstraint(name = "unique_dia", columnNames = "dia_semana")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HorarioTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El día de la semana es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 20)
    private DiaSemana diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean isHoraDentroDelRango(LocalTime hora) {
        return !hora.isBefore(horaInicio) && !hora.isAfter(horaFin);
    }
}