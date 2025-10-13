package com.example.proyectoingsoft2.model.cupos;

import com.example.proyectoingsoft2.model.personas.Estudiante;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Manager en memoria para Cupos y Reservas.
 */
public class FactoryGestionCupos {
    private final AtomicLong idGenCupos = new AtomicLong(1);
    private final AtomicLong idGenReservas = new AtomicLong(1);

    private final Map<Long, Cupo> cupos = new LinkedHashMap<>();
    private final Map<Long, ReservaCupo> reservas = new LinkedHashMap<>();

    // CRUD Cupo
    public Cupo createCupo(String estado, double valor, String grado) {
        long id = idGenCupos.getAndIncrement();
        Cupo c = new Cupo(id, estado, valor, grado);
        cupos.put(id, c);
        return c;
    }

    public Optional<Cupo> findCupoById(long id) { return Optional.ofNullable(cupos.get(id)); }
    public List<Cupo> listCupos() { return new ArrayList<>(cupos.values()); }

    public void updateCupo(Cupo c) {
        if (!cupos.containsKey(c.getId())) throw new NoSuchElementException("Cupo no existe: " + c.getId());
        cupos.put(c.getId(), c);
    }

    public void deleteCupo(long id) {
        cupos.remove(id);
        // opcional: eliminar reservas vinculadas (no automático aquí)
    }

    // CRUD Reserva
    public ReservaCupo createReserva(long cupoId, LocalDate fecha, Estudiante estudiante) {
        Cupo cupo = cupos.get(cupoId);
        if (cupo == null) throw new NoSuchElementException("No existe cupo " + cupoId);
        long id = idGenReservas.getAndIncrement();
        ReservaCupo r = new ReservaCupo(id, fecha, cupo, estudiante);
        reservas.put(id, r);
        // marcar cupo como reservado
        cupo.setEstado("RESERVADO");
        return r;
    }

    public Optional<ReservaCupo> findReservaById(long id) { return Optional.ofNullable(reservas.get(id)); }
    public List<ReservaCupo> listReservas() { return new ArrayList<>(reservas.values()); }

    public void updateReserva(ReservaCupo r) {
        if (!reservas.containsKey(r.getId())) throw new NoSuchElementException("Reserva no existe: " + r.getId());
        reservas.put(r.getId(), r);
    }

    public void deleteReserva(long id) {
        ReservaCupo r = reservas.remove(id);
        if (r != null && r.getCupo() != null) {
            // opcional: liberar cupo
            r.getCupo().setEstado("DISPONIBLE");
        }
    }

    // Búsquedas auxiliares
    public List<Cupo> findCuposByGrado(String grado) {
        return cupos.values().stream().filter(c -> Objects.equals(c.getGrado(), grado)).collect(Collectors.toList());
    }
}
