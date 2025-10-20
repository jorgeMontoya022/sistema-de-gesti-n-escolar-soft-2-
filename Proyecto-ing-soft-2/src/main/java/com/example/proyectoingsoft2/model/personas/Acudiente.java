package com.example.proyectoingsoft2.model.personas;

import java.util.Objects;

public class Acudiente {
    private long id;
    private String nombre;
    private String documento;
    private String telefono;
    private String correo;
    private String parentesco; // Padre, Madre, Abuelo, Tío, Tutor, etc.
    private String direccion;

    public Acudiente() {}

    public Acudiente(long id, String nombre, String documento, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.parentesco = "Padre/Madre";
    }

    public Acudiente(long id, String nombre, String documento, String telefono, String correo,
                     String parentesco, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.parentesco = parentesco;
        this.direccion = direccion;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Acudiente)) return false;
        Acudiente that = (Acudiente) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombre + " (" + parentesco + ")"; }
}