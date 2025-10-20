package com.example.proyectoingsoft2.model.contratos;

import com.example.proyectoingsoft2.model.personas.Acudiente;
import java.time.LocalDate;
import java.util.Objects;

public class Contrato {
    private long idContrato;
    private Acudiente acudiente;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado; // GENERADO, FIRMADO, VIGENTE, EXPIRADO
    private String detalleCondiciones;
    private String numeroComprobante;
    private boolean firmadoPorAcudiente;
    private boolean firmadoPorCoordinador;
    private boolean firmadoPorDirectora;

    public Contrato() {}

    public Contrato(long idContrato, Acudiente acudiente, LocalDate fechaEmision, String detalleCondiciones) {
        this.idContrato = idContrato;
        this.acudiente = acudiente;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaEmision.plusYears(1);
        this.estado = "GENERADO";
        this.detalleCondiciones = detalleCondiciones;
        this.numeroComprobante = "CTR-" + idContrato + "-" + System.currentTimeMillis();
        this.firmadoPorAcudiente = false;
        this.firmadoPorCoordinador = false;
        this.firmadoPorDirectora = false;
    }

    public long getIdContrato() { return idContrato; }
    public void setIdContrato(long idContrato) { this.idContrato = idContrato; }

    public Acudiente getAcudiente() { return acudiente; }
    public void setAcudiente(Acudiente acudiente) { this.acudiente = acudiente; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDetalleCondiciones() { return detalleCondiciones; }
    public void setDetalleCondiciones(String detalleCondiciones) { this.detalleCondiciones = detalleCondiciones; }

    public String getNumeroComprobante() { return numeroComprobante; }

    public boolean isFirmadoPorAcudiente() { return firmadoPorAcudiente; }
    public void setFirmadoPorAcudiente(boolean firmadoPorAcudiente) { this.firmadoPorAcudiente = firmadoPorAcudiente; }

    public boolean isFirmadoPorCoordinador() { return firmadoPorCoordinador; }
    public void setFirmadoPorCoordinador(boolean firmadoPorCoordinador) { this.firmadoPorCoordinador = firmadoPorCoordinador; }

    public boolean isFirmadoPorDirectora() { return firmadoPorDirectora; }
    public void setFirmadoPorDirectora(boolean firmadoPorDirectora) { this.firmadoPorDirectora = firmadoPorDirectora; }

    public boolean estanTodosFirmantes() {
        return firmadoPorAcudiente && firmadoPorCoordinador && firmadoPorDirectora;
    }

    public boolean esValido() {
        return "VIGENTE".equals(estado) && LocalDate.now().isBefore(fechaVencimiento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contrato)) return false;
        Contrato contrato = (Contrato) o;
        return idContrato == contrato.idContrato;
    }

    @Override
    public int hashCode() { return Objects.hash(idContrato); }

    @Override
    public String toString() {
        return "Contrato{" + "ID=" + idContrato + ", Acudiente=" + (acudiente != null ? acudiente.getNombre() : "null") +
                ", Estado=" + estado + ", Comprobante=" + numeroComprobante + "}";
    }
}