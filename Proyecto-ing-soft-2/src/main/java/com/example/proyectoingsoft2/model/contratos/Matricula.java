package com.example.proyectoingsoft2.model.contratos;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Directora;
import com.example.proyectoingsoft2.model.cupos.Cupo;
import java.time.LocalDate;
import java.util.Objects;

public class Matricula {
    private long idMatricula;
    private Estudiante estudiante;
    private Contrato contrato;
    private Cupo cupoAsignado;
    private Directora directoraAprobadora;
    private LocalDate fechaRegistro;
    private LocalDate fechaAprobacion;
    private String estado; // PENDIENTE, APROBADA, ACTIVA, RECHAZADA
    private double valorMatricula;
    private boolean pagoPendiente;
    private String numeroComprobante;
    private String descripcion;

    public Matricula() {}

    public Matricula(long idMatricula, Estudiante estudiante, Contrato contrato,
                     LocalDate fechaRegistro, double valorMatricula) {
        this.idMatricula = idMatricula;
        this.estudiante = estudiante;
        this.contrato = contrato;
        this.fechaRegistro = fechaRegistro;
        this.estado = "PENDIENTE";
        this.valorMatricula = valorMatricula;
        this.pagoPendiente = true;
        this.numeroComprobante = "MAT-" + idMatricula + "-" + System.currentTimeMillis();
        this.descripcion = "";
        this.cupoAsignado = null; // Se asignará cuando sea aprobada
    }

    // Getters y Setters
    public long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(long idMatricula) { this.idMatricula = idMatricula; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Contrato getContrato() { return contrato; }
    public void setContrato(Contrato contrato) { this.contrato = contrato; }

    // NUEVO: Cupo Asignado
    public Cupo getCupoAsignado() { return cupoAsignado; }
    public void setCupoAsignado(Cupo cupoAsignado) { this.cupoAsignado = cupoAsignado; }

    public Directora getDirectoraAprobadora() { return directoraAprobadora; }
    public void setDirectoraAprobadora(Directora directoraAprobadora) {
        this.directoraAprobadora = directoraAprobadora;
    }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDate fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getValorMatricula() { return valorMatricula; }
    public void setValorMatricula(double valorMatricula) {
        this.valorMatricula = valorMatricula;
    }

    public boolean isPagoPendiente() { return pagoPendiente; }
    public void setPagoPendiente(boolean pagoPendiente) {
        this.pagoPendiente = pagoPendiente;
    }

    public String getNumeroComprobante() { return numeroComprobante; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Método auxiliar para obtener información del cupo
    public String getCupoInfo() {
        if (cupoAsignado == null) return "Sin asignar";
        return cupoAsignado.getGrado() + " - " + cupoAsignado.getJornada();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matricula)) return false;
        Matricula matricula = (Matricula) o;
        return idMatricula == matricula.idMatricula;
    }

    @Override
    public int hashCode() { return Objects.hash(idMatricula); }

    @Override
    public String toString() {
        return "Matricula{" + "ID=" + idMatricula + ", Estudiante=" +
                (estudiante != null ? estudiante.getNombre() : "null") +
                ", Estado=" + estado + ", Cupo=" + getCupoInfo() + "}";
    }
}