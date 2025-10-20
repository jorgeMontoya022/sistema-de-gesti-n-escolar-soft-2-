package com.example.proyectoingsoft2.model.personas;

public class Directora extends Administrador {
    private String nivelAutorizacion;

    public Directora() {}

    public Directora(long id, String nombre, String usuario, String contrasena) {
        super(id, nombre, usuario, contrasena, "DIRECTORA");
        this.nivelAutorizacion = "ALTA";
    }

    public String getNivelAutorizacion() { return nivelAutorizacion; }
    public void setNivelAutorizacion(String nivelAutorizacion) { this.nivelAutorizacion = nivelAutorizacion; }

    @Override
    public String toString() {
        return "Directora: " + getNombre() + " (Nivel: " + nivelAutorizacion + ")";
    }
}