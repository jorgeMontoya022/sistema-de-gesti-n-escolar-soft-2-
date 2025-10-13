package com.example.proyectoingsoft2.model.personas;

import java.util.Objects;

public class Estudiante {
    private long id;
    private String nombre;
    private String documento;
    private String programa;
    private String estadoMatricula;

    public Estudiante() {}

    public Estudiante(long id, String nombre, String documento, String programa) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.programa = programa;
        this.estadoMatricula = "Pendiente";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }

    public String getEstadoMatricula() { return estadoMatricula; }
    public void setEstadoMatricula(String estadoMatricula) { this.estadoMatricula = estadoMatricula; }

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
        return nombre + " (" + documento + ")";
    }
}
