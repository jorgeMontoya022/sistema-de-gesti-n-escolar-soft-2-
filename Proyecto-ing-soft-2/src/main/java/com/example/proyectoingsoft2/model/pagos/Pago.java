package com.example.proyectoingsoft2.model.pagos;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Pago con campos según el diagrama: idPago, monto, fechaPago, estado, metodoPago, comprobante, descripcion
 */
public class Pago {
    private long idPago;
    private double monto;
    private LocalDate fechaPago;
    private String estado; // p. ej. PENDIENTE, COMPLETADO, RECHAZADO
    private String metodoPago;
    private String comprobante; // ruta o identificador del comprobante
    private String descripcion;

    // opcional: vinculación a matricula/contrato (no incluida por simplicidad)
    private long referenciaId; // id de la entidad a la que se aplica (matricula, contrato, etc.)
    private String referenciaTipo; // e.g., "MATRICULA"

    public Pago() {}

    public Pago(long idPago, double monto, LocalDate fechaPago, String estado, String metodoPago, String comprobante, String descripcion) {
        this.idPago = idPago;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.estado = estado;
        this.metodoPago = metodoPago;
        this.comprobante = comprobante;
        this.descripcion = descripcion;
    }

    public long getIdPago() { return idPago; }
    public void setIdPago(long idPago) { this.idPago = idPago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(long referenciaId) { this.referenciaId = referenciaId; }

    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago)) return false;
        Pago pago = (Pago) o;
        return idPago == pago.idPago;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPago);
    }

    @Override
    public String toString() {
        return "Pago{" + "idPago=" + idPago + ", monto=" + monto +
                ", fechaPago=" + fechaPago + ", estado='" + estado + '\'' +
                ", metodoPago='" + metodoPago + '\'' + ", comprobante='" + comprobante + '\'' +
                ", descripcion='" + descripcion + '\'' + ", referenciaTipo='" + referenciaTipo + '\'' +
                ", referenciaId=" + referenciaId + '}';
    }
}
