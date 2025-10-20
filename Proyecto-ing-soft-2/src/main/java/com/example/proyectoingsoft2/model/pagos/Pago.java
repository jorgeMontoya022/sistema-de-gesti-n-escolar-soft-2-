package com.example.proyectoingsoft2.model.pagos;

import java.time.LocalDate;
import java.util.Objects;

public class Pago {
    private long idPago;
    private long idMatricula;
    private double monto;
    private LocalDate fechaPago;
    private String estado; // PENDIENTE, COMPLETADO, RECHAZADO, REEMBOLSADO
    private String metodoPago; // EFECTIVO, TRANSFERENCIA, TARJETA, PSE, CUOTAS
    private String numeroTransaccion;
    private String comprobante;
    private String descripcion;
    private LocalDate fechaReembolso;

    public Pago() {}

    public Pago(long idPago, long idMatricula, double monto, String metodoPago, String descripcion) {
        this.idPago = idPago;
        this.idMatricula = idMatricula;
        this.monto = monto;
        this.fechaPago = LocalDate.now();
        this.estado = "PENDIENTE";
        this.metodoPago = metodoPago;
        this.descripcion = descripcion;
        this.numeroTransaccion = "TRX-" + idPago + "-" + System.currentTimeMillis();
        this.comprobante = "CPR-" + idPago + "-" + System.currentTimeMillis();
    }

    public long getIdPago() { return idPago; }
    public void setIdPago(long idPago) { this.idPago = idPago; }

    public long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(long idMatricula) { this.idMatricula = idMatricula; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getNumeroTransaccion() { return numeroTransaccion; }
    public void setNumeroTransaccion(String numeroTransaccion) { this.numeroTransaccion = numeroTransaccion; }

    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaReembolso() { return fechaReembolso; }
    public void setFechaReembolso(LocalDate fechaReembolso) { this.fechaReembolso = fechaReembolso; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago)) return false;
        Pago pago = (Pago) o;
        return idPago == pago.idPago;
    }

    @Override
    public int hashCode() { return Objects.hash(idPago); }

    @Override
    public String toString() {
        return "Pago{" + "ID=" + idPago + ", Monto=" + monto + ", Estado=" + estado +
                ", Transacción=" + numeroTransaccion + "}";
    }
}