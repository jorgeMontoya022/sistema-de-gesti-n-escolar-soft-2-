package com.example.proyectoingsoft2.model.contratos;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Directora;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Matrícula asociada a un estudiante y (opcionalmente) a un contrato.
 * Campos: idMatricula, fechaRegistro, estado, valorMatricula
 */
public class Matricula {
    private long idMatricula;
    private LocalDate fechaRegistro;
    private String estado;
    private double valorMatricula;

    private Estudiante estudiante; // quién está matriculado
    private Contrato contrato;     // contrato asociado (si aplica)
    private Directora directora;   // directora que aprueba (según diagrama, 1 director)

    public Matricula() {}

    public Matricula(long idMatricula, LocalDate fechaRegistro, String estado, double valorMatricula,
                     Estudiante estudiante, Contrato contrato, Directora directora) {
        this.idMatricula = idMatricula;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.valorMatricula = valorMatricula;
        this.estudiante = estudiante;
        this.contrato = contrato;
        this.directora = directora;
    }

    public long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(long idMatricula) { this.idMatricula = idMatricula; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getValorMatricula() { return valorMatricula; }
    public void setValorMatricula(double valorMatricula) { this.valorMatricula = valorMatricula; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Contrato getContrato() { return contrato; }
    public void setContrato(Contrato contrato) { this.contrato = contrato; }

    public Directora getDirectora() { return directora; }
    public void setDirectora(Directora directora) { this.directora = directora; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matricula)) return false;
        Matricula matricula = (Matricula) o;
        return idMatricula == matricula.idMatricula;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMatricula);
    }

    @Override
    public String toString() {
        return "Matricula{" + "idMatricula=" + idMatricula +
                ", fechaRegistro=" + fechaRegistro + ", estado='" + estado + '\'' +
                ", valorMatricula=" + valorMatricula +
                ", estudiante=" + (estudiante != null ? estudiante.getId() : "null") +
                ", contrato=" + (contrato != null ? contrato.getIdContrato() : "null") +
                ", directora=" + (directora != null ? directora.getId() : "null") + '}';
    }
}
