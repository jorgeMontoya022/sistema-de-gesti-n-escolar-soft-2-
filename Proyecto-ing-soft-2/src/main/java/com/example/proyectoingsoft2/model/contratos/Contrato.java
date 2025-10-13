package com.example.proyectoingsoft2.model.contratos;

import com.example.proyectoingsoft2.model.personas.Acudiente;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Contrato que puede estar relacionado con un acudiente (quien firma).
 * Campos basados en diagrama: idContrato, fechaEmision, fechaVencimiento, detalleCondiciones
 */
public class Contrato {
    private long idContrato;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String detalleCondiciones;
    private Acudiente acudiente; // quien firma el contrato (según diagrama)

    public Contrato() {}

    public Contrato(long idContrato, LocalDate fechaEmision, LocalDate fechaVencimiento, String detalleCondiciones, Acudiente acudiente) {
        this.idContrato = idContrato;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.detalleCondiciones = detalleCondiciones;
        this.acudiente = acudiente;
    }

    public long getIdContrato() { return idContrato; }
    public void setIdContrato(long idContrato) { this.idContrato = idContrato; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getDetalleCondiciones() { return detalleCondiciones; }
    public void setDetalleCondiciones(String detalleCondiciones) { this.detalleCondiciones = detalleCondiciones; }

    public Acudiente getAcudiente() { return acudiente; }
    public void setAcudiente(Acudiente acudiente) { this.acudiente = acudiente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contrato)) return false;
        Contrato contrato = (Contrato) o;
        return idContrato == contrato.idContrato;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idContrato);
    }

    @Override
    public String toString() {
        return "Contrato{" + "idContrato=" + idContrato +
                ", fechaEmision=" + fechaEmision +
                ", fechaVencimiento=" + fechaVencimiento +
                ", detalle='" + detalleCondiciones + '\'' +
                ", acudiente=" + (acudiente != null ? acudiente.getId() : "null") + '}';
    }
}
