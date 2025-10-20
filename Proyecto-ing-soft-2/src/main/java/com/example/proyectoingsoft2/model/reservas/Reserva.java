package com.example.proyectoingsoft2.model.reservas;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Acudiente;
import com.example.proyectoingsoft2.model.cupos.Cupo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase Reserva - Representa una reserva temporal de cupo
 * Funcionalidad 3: Reservar Cupo
 */
public class Reserva {
    private long idReserva;
    private Estudiante estudiante;
    private Acudiente acudiente;
    private Cupo cupoReservado;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaExpiracion; // Reserva válida por X días
    private String estado; // ACTIVA, CONFIRMADA, EXPIRADA, CANCELADA
    private String numeroReserva; // Número único de comprobante
    private String gradoSolicitado;
    private String jornadaSolicitada;
    private String observaciones;

    public Reserva() {}

    public Reserva(long idReserva, Estudiante estudiante, Acudiente acudiente,
                   String gradoSolicitado, String jornadaSolicitada) {
        this.idReserva = idReserva;
        this.estudiante = estudiante;
        this.acudiente = acudiente;
        this.gradoSolicitado = gradoSolicitado;
        this.jornadaSolicitada = jornadaSolicitada;
        this.fechaReserva = LocalDateTime.now();
        this.fechaExpiracion = LocalDateTime.now().plusDays(15); // 15 días para completar matrícula
        this.estado = "ACTIVA";
        this.numeroReserva = "RES-" + idReserva + "-" + System.currentTimeMillis();
        this.observaciones = "";
    }

    // Getters y Setters
    public long getIdReserva() { return idReserva; }
    public void setIdReserva(long idReserva) { this.idReserva = idReserva; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Acudiente getAcudiente() { return acudiente; }
    public void setAcudiente(Acudiente acudiente) { this.acudiente = acudiente; }

    public Cupo getCupoReservado() { return cupoReservado; }
    public void setCupoReservado(Cupo cupoReservado) { this.cupoReservado = cupoReservado; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNumeroReserva() { return numeroReserva; }
    public void setNumeroReserva(String numeroReserva) { this.numeroReserva = numeroReserva; }

    public String getGradoSolicitado() { return gradoSolicitado; }
    public void setGradoSolicitado(String gradoSolicitado) { this.gradoSolicitado = gradoSolicitado; }

    public String getJornadaSolicitada() { return jornadaSolicitada; }
    public void setJornadaSolicitada(String jornadaSolicitada) {
        this.jornadaSolicitada = jornadaSolicitada;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Métodos de negocio
    public boolean estaVigente() {
        return "ACTIVA".equals(estado) && LocalDateTime.now().isBefore(fechaExpiracion);
    }

    public boolean haExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion) && "ACTIVA".equals(estado);
    }

    public long getDiasRestantes() {
        if (!estaVigente()) return 0;
        return java.time.Duration.between(LocalDateTime.now(), fechaExpiracion).toDays();
    }

    public String getCupoInfo() {
        if (cupoReservado == null) {
            return gradoSolicitado + " - " + jornadaSolicitada + " (Pendiente asignación)";
        }
        return cupoReservado.getGrado() + " - " + cupoReservado.getJornada();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva)) return false;
        Reserva reserva = (Reserva) o;
        return idReserva == reserva.idReserva;
    }

    @Override
    public int hashCode() { return Objects.hash(idReserva); }

    @Override
    public String toString() {
        return "Reserva{" + "Número=" + numeroReserva +
                ", Estudiante=" + (estudiante != null ? estudiante.getNombre() : "null") +
                ", Estado=" + estado + ", Cupo=" + getCupoInfo() + "}";
    }
}