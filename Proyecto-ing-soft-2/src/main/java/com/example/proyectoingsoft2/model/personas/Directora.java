package com.example.proyectoingsoft2.model.personas;

public class Directora extends Administrador {
    private String nivelAutorizacion;

    public Directora(long id, String nombre, String usuario, String contrasena) {
        super(id, nombre, usuario, contrasena);
        this.nivelAutorizacion = "Alta";
    }

    public String getNivelAutorizacion() { return nivelAutorizacion; }
}
