package com.example.proyectoingsoft2.model.personas;

import java.util.Objects;

public class Estudiante {
    private long id;
    private String nombre;
    private String documento;
    private String grado; // Pre-jardín a Quinto
    private String estadoMatricula; // PENDIENTE, APROBADA, ACTIVA, RECHAZADA
    private Acudiente acudiente; // Relación: Un estudiante tiene UN acudiente
    private long idAcudiente; // Para referencia

    public Estudiante() {}

    public Estudiante(long id, String nombre, String documento, String grado, Acudiente acudiente) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.grado = grado;
        this.acudiente = acudiente;
        this.idAcudiente = acudiente != null ? acudiente.getId() : -1;
        this.estadoMatricula = "PENDIENTE";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getEstadoMatricula() { return estadoMatricula; }
    public void setEstadoMatricula(String estadoMatricula) { this.estadoMatricula = estadoMatricula; }

    public Acudiente getAcudiente() { return acudiente; }
    public void setAcudiente(Acudiente acudiente) {
        this.acudiente = acudiente;
        this.idAcudiente = acudiente != null ? acudiente.getId() : -1;
    }

    public long getIdAcudiente() { return idAcudiente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudiante)) return false;
        Estudiante that = (Estudiante) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return nombre + " (Grado: " + grado + ")";
    }
}