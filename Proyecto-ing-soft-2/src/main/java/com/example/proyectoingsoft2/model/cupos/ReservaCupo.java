package com.example.proyectoingsoft2.model.cupos;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import java.time.LocalDate;
import java.util.Objects;

public class ReservaCupo {
    private long id;
    private Cupo cupo;
    private Estudiante estudiante;
    private LocalDate fechaReserva;
    private LocalDate fechaVencimientoReserva;
    private String estado; // ACTIVA, CONFIRMADA, CANCELADA, EXPIRADA

    public ReservaCupo() {}

    public ReservaCupo(long id, Cupo cupo, Estudiante estudiante, LocalDate fechaReserva) {
        this.id = id;
        this.cupo = cupo;
        this.estudiante = estudiante;
        this.fechaReserva = fechaReserva;
        this.fechaVencimientoReserva = fechaReserva.plusDays(15); // Válida por 15 días
        this.estado = "ACTIVA";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Cupo getCupo() { return cupo; }
    public void setCupo(Cupo cupo) { this.cupo = cupo; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalDate getFechaVencimientoReserva() { return fechaVencimientoReserva; }
    public void setFechaVencimientoReserva(LocalDate fechaVencimientoReserva) { this.fechaVencimientoReserva = fechaVencimientoReserva; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean estaVigente() {
        return LocalDate.now().isBefore(fechaVencimientoReserva) && "ACTIVA".equals(estado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservaCupo)) return false;
        ReservaCupo that = (ReservaCupo) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Reserva{" + "ID=" + id + ", Estudiante=" + (estudiante != null ? estudiante.getNombre() : "null") +
                ", Cupo=" + (cupo != null ? cupo.getGrado() : "null") + ", Estado=" + estado + "}";
    }
}