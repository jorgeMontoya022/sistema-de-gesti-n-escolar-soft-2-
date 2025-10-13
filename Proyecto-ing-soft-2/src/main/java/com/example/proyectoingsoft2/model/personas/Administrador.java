package com.example.proyectoingsoft2.model.personas;

public class Administrador {
    private long id;
    private String nombre;
    private String usuario;
    private String contrasena;

    public Administrador() {}

    public Administrador(long id, String nombre, String usuario, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        return this.usuario.equals(usuario) && this.contrasena.equals(contrasena);
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }
}
