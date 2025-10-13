package com.example.proyectoingsoft2.model.personas;

public class FactoryPersonas {

    public static Estudiante crearEstudiante(long id, String nombre, String documento, String programa) {
        return new Estudiante(id, nombre, documento, programa);
    }

    public static Acudiente crearAcudiente(long id, String nombre, String documento, String telefono, String correo) {
        return new Acudiente(id, nombre, documento, telefono, correo);
    }

    public static Administrador crearAdministrador(long id, String nombre, String usuario, String contrasena) {
        return new Administrador(id, nombre, usuario, contrasena);
    }

    public static Directora crearDirectora(long id, String nombre, String usuario, String contrasena) {
        return new Directora(id, nombre, usuario, contrasena);
    }
}
