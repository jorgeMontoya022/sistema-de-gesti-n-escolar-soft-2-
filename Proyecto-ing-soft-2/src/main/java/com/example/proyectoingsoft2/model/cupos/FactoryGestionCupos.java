package com.example.proyectoingsoft2.model.cupos;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FactoryGestionCupos {
    // ===== SINGLETON =====
    private static FactoryGestionCupos instance;

    private final AtomicLong idGen = new AtomicLong(1000);
    private final Map<Long, Cupo> cupos = new LinkedHashMap<>();

    // Constructor PRIVADO para Singleton
    private FactoryGestionCupos() {}

    // Método para obtener la única instancia
    public static FactoryGestionCupos getInstance() {
        if (instance == null) {
            instance = new FactoryGestionCupos();
        }
        return instance;
    }

    // ===== CRUD CUPO =====
    public Cupo createCupo(String grado, double valor, String jornada, int cuposDisponibles) {
        long id = idGen.getAndIncrement();
        Cupo cupo = new Cupo(id, grado, valor, "DISPONIBLE", jornada, cuposDisponibles);
        cupos.put(id, cupo);
        return cupo;
    }

    public Optional<Cupo> findCupoById(long id) {
        return Optional.ofNullable(cupos.get(id));
    }

    public List<Cupo> listCupos() {
        return new ArrayList<>(cupos.values());
    }

    public void updateCupo(Cupo cupo) {
        if (!cupos.containsKey(cupo.getId()))
            throw new NoSuchElementException("Cupo no existe: " + cupo.getId());
        cupos.put(cupo.getId(), cupo);
    }

    public void deleteCupo(long id) {
        cupos.remove(id);
    }

    public List<Cupo> findCuposByGrado(String grado) {
        return cupos.values().stream()
                .filter(c -> c.getGrado().equalsIgnoreCase(grado))
                .collect(Collectors.toList());
    }

    public List<Cupo> findCuposDisponibles() {
        return cupos.values().stream()
                .filter(Cupo::hayDisponibilidad)
                .collect(Collectors.toList());
    }

    public List<Cupo> findCuposByJornada(String jornada) {
        return cupos.values().stream()
                .filter(c -> c.getJornada().equalsIgnoreCase(jornada))
                .collect(Collectors.toList());
    }

    public Optional<Cupo> findCupoDisponibleByGradoYJornada(String grado) {
        return cupos.values().stream()
                .filter(c -> c.getGrado().equalsIgnoreCase(grado))
                .filter(Cupo::hayDisponibilidad)
                .findFirst();
    }

    public Map<Long, Cupo> getCupos() {
        return new HashMap<>(cupos);
    }
}