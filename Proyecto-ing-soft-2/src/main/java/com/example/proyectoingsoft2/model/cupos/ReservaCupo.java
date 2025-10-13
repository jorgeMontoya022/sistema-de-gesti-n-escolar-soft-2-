package com.example.proyectoingsoft2.model.cupos;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Reserva de un cupo por un estudiante (según diagrama).
 * Guarda referencia al cupo y al estudiante (opcionalmente).
 */
public class ReservaCupo {
    private long id;
    private LocalDate fecha;
    private Cupo cupo;
    private Estudiante estudiante; // quien reservó (puede ser null si se maneja diferente)

    public ReservaCupo() {}

    public ReservaCupo(long id, LocalDate fecha, Cupo cupo, Estudiante estudiante) {
        this.id = id;
        this.fecha = fecha;
        this.cupo = cupo;
        this.estudiante = estudiante;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Cupo getCupo() { return cupo; }
    public void setCupo(Cupo cupo) { this.cupo = cupo; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservaCupo)) return false;
        ReservaCupo that = (ReservaCupo) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReservaCupo{" + "id=" + id + ", fecha=" + fecha +
                ", cupo=" + (cupo != null ? cupo.getId() : "null") +
                ", estudiante=" + (estudiante != null ? estudiante.getId() : "null") + '}';
    }
}
