package com.example.proyectoingsoft2.model.personas;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FactoryPersonas {
    private static final AtomicLong idGenEstudiante = new AtomicLong(1000);
    private static final AtomicLong idGenAcudiente = new AtomicLong(2000);
    private static final AtomicLong idGenAdministrador = new AtomicLong(3000);

    private static final Map<Long, Estudiante> estudiantes = new LinkedHashMap<>();
    private static final Map<Long, Acudiente> acudientes = new LinkedHashMap<>();
    private static final Map<Long, Administrador> administradores = new LinkedHashMap<>();

    // CRUD Estudiante
    public static Estudiante crearEstudiante(String nombre, String documento, String grado, Acudiente acudiente) {
        long id = idGenEstudiante.getAndIncrement();
        Estudiante e = new Estudiante(id, nombre, documento, grado, acudiente);
        estudiantes.put(id, e);
        return e;
    }

    public static Optional<Estudiante> findEstudianteById(long id) {
        return Optional.ofNullable(estudiantes.get(id));
    }

    public static List<Estudiante> listEstudiantes() {
        return new ArrayList<>(estudiantes.values());
    }

    public static void updateEstudiante(Estudiante e) {
        if (!estudiantes.containsKey(e.getId()))
            throw new NoSuchElementException("Estudiante no existe: " + e.getId());
        estudiantes.put(e.getId(), e);
    }

    public static void deleteEstudiante(long id) {
        estudiantes.remove(id);
    }

    public static List<Estudiante> findEstudiantesByGrado(String grado) {
        return estudiantes.values().stream()
                .filter(e -> e.getGrado().equals(grado))
                .toList();
    }

    // CRUD Acudiente
    public static Acudiente crearAcudiente(String nombre, String documento, String telefono,
                                           String correo, String parentesco, String direccion) {
        long id = idGenAcudiente.getAndIncrement();
        Acudiente a = new Acudiente(id, nombre, documento, telefono, correo, parentesco, direccion);
        acudientes.put(id, a);
        return a;
    }

    public static Optional<Acudiente> findAcudienteById(long id) {
        return Optional.ofNullable(acudientes.get(id));
    }

    public static List<Acudiente> listAcudientes() {
        return new ArrayList<>(acudientes.values());
    }

    public static void updateAcudiente(Acudiente a) {
        if (!acudientes.containsKey(a.getId()))
            throw new NoSuchElementException("Acudiente no existe: " + a.getId());
        acudientes.put(a.getId(), a);
    }

    public static void deleteAcudiente(long id) {
        acudientes.remove(id);
    }

    // CRUD Administrador
    public static Administrador crearAdministrador(String nombre, String usuario, String contrasena, String rol) {
        long id = idGenAdministrador.getAndIncrement();
        Administrador adm = new Administrador(id, nombre, usuario, contrasena, rol);
        administradores.put(id, adm);
        return adm;
    }

    public static Directora crearDirectora(String nombre, String usuario, String contrasena) {
        long id = idGenAdministrador.getAndIncrement();
        Directora dir = new Directora(id, nombre, usuario, contrasena);
        administradores.put(id, dir);
        return dir;
    }

    public static Optional<Administrador> findAdministradorById(long id) {
        return Optional.ofNullable(administradores.get(id));
    }

    public static List<Administrador> listAdministradores() {
        return new ArrayList<>(administradores.values());
    }

    public static Optional<Administrador> findAdministradorByCredenciales(String usuario, String contrasena) {
        return administradores.values().stream()
                .filter(a -> a.validarCredenciales(usuario, contrasena))
                .findFirst();
    }

    public static Map<Long, Estudiante> getEstudiantes() {
        return new HashMap<>(estudiantes);
    }

    public static Map<Long, Acudiente> getAcudientes() {
        return new HashMap<>(acudientes);
    }
}