package com.example.proyectoingsoft2.model.reservas;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Acudiente;
import com.example.proyectoingsoft2.model.cupos.Cupo;
import com.example.proyectoingsoft2.model.cupos.FactoryGestionCupos;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Factory para gestión de reservas de cupos
 * Implementa Funcionalidad 3: Reservar Cupo
 */
public class FactoryGestionReservas {
    private static FactoryGestionReservas instance;

    private final AtomicLong idGenReserva = new AtomicLong(9000);
    private final Map<Long, Reserva> reservas = new LinkedHashMap<>();
    private final FactoryGestionCupos factoryCupos;

    private FactoryGestionReservas() {
        this.factoryCupos = FactoryGestionCupos.getInstance();
    }

    public static FactoryGestionReservas getInstance() {
        if (instance == null) {
            instance = new FactoryGestionReservas();
        }
        return instance;
    }

    // ===== CRUD RESERVA =====

    /**
     * Crea una reserva de cupo
     * Valida que no existan múltiples reservas activas para el mismo estudiante
     */
    public Reserva createReserva(Estudiante estudiante, Acudiente acudiente,
                                 String gradoSolicitado, String jornadaSolicitada) {

        // Validación: No permitir múltiples reservas activas del mismo estudiante
        if (tieneReservaActiva(estudiante.getId())) {
            throw new IllegalStateException(
                    "El estudiante " + estudiante.getNombre() +
                            " ya tiene una reserva activa. Debe completarla o cancelarla primero."
            );
        }

        // Validar disponibilidad de cupo
        if (!verificarDisponibilidadCupo(gradoSolicitado, jornadaSolicitada)) {
            throw new IllegalArgumentException(
                    "No hay cupos disponibles para " + gradoSolicitado +
                            " en jornada " + jornadaSolicitada
            );
        }

        // Crear reserva
        long id = idGenReserva.getAndIncrement();
        Reserva reserva = new Reserva(id, estudiante, acudiente, gradoSolicitado, jornadaSolicitada);

        // Buscar y reservar temporalmente el cupo
        Optional<Cupo> cupoOpt = factoryCupos.findCupoDisponibleByGradoYJornada(
                gradoSolicitado
        );

        if (cupoOpt.isPresent()) {
            Cupo cupo = cupoOpt.get();
            reserva.setCupoReservado(cupo);
            // Nota: El cupo NO se ocupa aún, solo se marca la reserva
        }

        reservas.put(id, reserva);
        return reserva;
    }

    public Optional<Reserva> findReservaById(long id) {
        return Optional.ofNullable(reservas.get(id));
    }

    public Optional<Reserva> findReservaByNumero(String numeroReserva) {
        return reservas.values().stream()
                .filter(r -> r.getNumeroReserva().equals(numeroReserva))
                .findFirst();
    }

    public List<Reserva> listReservas() {
        return new ArrayList<>(reservas.values());
    }

    public List<Reserva> listReservasActivas() {
        return reservas.values().stream()
                .filter(Reserva::estaVigente)
                .collect(Collectors.toList());
    }

    public List<Reserva> listReservasExpiradas() {
        return reservas.values().stream()
                .filter(Reserva::haExpirado)
                .collect(Collectors.toList());
    }

    public List<Reserva> listReservasByEstudiante(long estudianteId) {
        return reservas.values().stream()
                .filter(r -> r.getEstudiante() != null &&
                        r.getEstudiante().getId() == estudianteId)
                .collect(Collectors.toList());
    }

    public void updateReserva(Reserva reserva) {
        if (!reservas.containsKey(reserva.getIdReserva()))
            throw new NoSuchElementException("Reserva no existe: " + reserva.getIdReserva());
        reservas.put(reserva.getIdReserva(), reserva);
    }

    /**
     * Confirma una reserva (cuando se completa la matrícula)
     */
    public void confirmarReserva(long idReserva) {
        Reserva reserva = reservas.get(idReserva);
        if (reserva == null) throw new NoSuchElementException("Reserva no existe");

        if (!reserva.estaVigente()) {
            throw new IllegalStateException("La reserva ha expirado o no está activa");
        }

        reserva.setEstado("CONFIRMADA");
        reservas.put(idReserva, reserva);
    }

    /**
     * Cancela una reserva
     */
    public void cancelarReserva(long idReserva, String motivo) {
        Reserva reserva = reservas.get(idReserva);
        if (reserva == null) throw new NoSuchElementException("Reserva no existe");

        reserva.setEstado("CANCELADA");
        reserva.setObservaciones("Cancelada: " + motivo);
        reservas.put(idReserva, reserva);
    }

    /**
     * Expira reservas automáticamente
     */
    public void expirarReservasVencidas() {
        reservas.values().stream()
                .filter(r -> "ACTIVA".equals(r.getEstado()))
                .filter(r -> LocalDateTime.now().isAfter(r.getFechaExpiracion()))
                .forEach(r -> {
                    r.setEstado("EXPIRADA");
                    r.setObservaciones("Expirada automáticamente por tiempo");
                });
    }

    public void deleteReserva(long id) {
        reservas.remove(id);
    }

    // ===== MÉTODOS DE VALIDACIÓN =====

    /**
     * Verifica si un estudiante tiene una reserva activa
     */
    public boolean tieneReservaActiva(long estudianteId) {
        return reservas.values().stream()
                .anyMatch(r -> r.getEstudiante() != null &&
                        r.getEstudiante().getId() == estudianteId &&
                        r.estaVigente());
    }

    /**
     * Obtiene la reserva activa de un estudiante
     */
    public Optional<Reserva> obtenerReservaActiva(long estudianteId) {
        return reservas.values().stream()
                .filter(r -> r.getEstudiante() != null &&
                        r.getEstudiante().getId() == estudianteId &&
                        r.estaVigente())
                .findFirst();
    }

    /**
     * Verifica disponibilidad de cupo considerando reservas
     */
    public boolean verificarDisponibilidadCupo(String grado, String jornada) {
        // Contar reservas activas para ese cupo
        long reservasActivas = reservas.values().stream()
                .filter(r -> r.estaVigente())
                .filter(r -> r.getGradoSolicitado().equalsIgnoreCase(grado))
                .filter(r -> r.getJornadaSolicitada().equalsIgnoreCase(jornada))
                .count();

        // Obtener cupo disponible
        Optional<Cupo> cupoOpt = factoryCupos.findCupoDisponibleByGradoYJornada(grado);

        if (!cupoOpt.isPresent()) return false;

        Cupo cupo = cupoOpt.get();
        int disponibles = cupo.getCuposDisponibles() - cupo.getCuposOcupados();

        // Considerar reservas activas como "bloqueadas"
        return disponibles > reservasActivas;
    }

    /**
     * Obtiene información de disponibilidad incluyendo reservas
     */
    public String obtenerInfoDisponibilidad(String grado, String jornada) {
        Optional<Cupo> cupoOpt = factoryCupos.findCupoDisponibleByGradoYJornada(grado);

        if (!cupoOpt.isPresent()) {
            return "No hay cupos disponibles para " + grado + " en jornada " + jornada;
        }

        Cupo cupo = cupoOpt.get();
        int disponibles = cupo.getCuposDisponibles() - cupo.getCuposOcupados();

        long reservasActivas = reservas.values().stream()
                .filter(r -> r.estaVigente())
                .filter(r -> r.getGradoSolicitado().equalsIgnoreCase(grado))
                .filter(r -> r.getJornadaSolicitada().equalsIgnoreCase(jornada))
                .count();

        int libres = disponibles - (int) reservasActivas;

        return String.format(
                "Total: %d | Ocupados: %d | Reservados: %d | Libres: %d",
                cupo.getCuposDisponibles(),
                cupo.getCuposOcupados(),
                reservasActivas,
                libres
        );
    }

    // ===== ESTADÍSTICAS =====

    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total Reservas", reservas.size());
        stats.put("Activas", (int) reservas.values().stream()
                .filter(r -> "ACTIVA".equals(r.getEstado())).count());
        stats.put("Confirmadas", (int) reservas.values().stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado())).count());
        stats.put("Expiradas", (int) reservas.values().stream()
                .filter(r -> "EXPIRADA".equals(r.getEstado())).count());
        stats.put("Canceladas", (int) reservas.values().stream()
                .filter(r -> "CANCELADA".equals(r.getEstado())).count());
        return stats;
    }

    public Map<Long, Reserva> getReservas() {
        return new HashMap<>(reservas);
    }
}