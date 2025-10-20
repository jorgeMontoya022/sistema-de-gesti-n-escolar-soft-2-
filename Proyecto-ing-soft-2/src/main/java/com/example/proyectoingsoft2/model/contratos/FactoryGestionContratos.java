package com.example.proyectoingsoft2.model.contratos;

import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Acudiente;
import com.example.proyectoingsoft2.model.personas.Directora;
import com.example.proyectoingsoft2.model.cupos.Cupo;
import com.example.proyectoingsoft2.model.cupos.FactoryGestionCupos;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FactoryGestionContratos {
    private static FactoryGestionContratos instance;

    private final AtomicLong idGenContrato = new AtomicLong(6000);
    private final AtomicLong idGenMatricula = new AtomicLong(7000);

    private final Map<Long, Contrato> contratos = new LinkedHashMap<>();
    private final Map<Long, Matricula> matriculas = new LinkedHashMap<>();

    private final FactoryGestionCupos factoryCupos;

    private FactoryGestionContratos() {
        this.factoryCupos = FactoryGestionCupos.getInstance();
    }

    public static FactoryGestionContratos getInstance() {
        if (instance == null) {
            instance = new FactoryGestionContratos();
        }
        return instance;
    }

    // ===== CRUD CONTRATO =====
    public Contrato createContrato(Acudiente acudiente, String detalleCondiciones) {
        long id = idGenContrato.getAndIncrement();
        Contrato c = new Contrato(id, acudiente, LocalDate.now(), detalleCondiciones);
        contratos.put(id, c);
        return c;
    }

    public Optional<Contrato> findContratoById(long id) {
        return Optional.ofNullable(contratos.get(id));
    }

    public List<Contrato> listContratos() {
        return new ArrayList<>(contratos.values());
    }

    public void updateContrato(Contrato c) {
        if (!contratos.containsKey(c.getIdContrato()))
            throw new NoSuchElementException("Contrato no existe: " + c.getIdContrato());
        contratos.put(c.getIdContrato(), c);
    }

    public void deleteContrato(long id) {
        contratos.remove(id);
    }

    public void firmarContrato(long idContrato, String tipoFirmante) {
        Contrato c = contratos.get(idContrato);
        if (c == null) throw new NoSuchElementException("Contrato no existe");

        switch (tipoFirmante) {
            case "ACUDIENTE":
                c.setFirmadoPorAcudiente(true);
                break;
            case "COORDINADOR":
                c.setFirmadoPorCoordinador(true);
                break;
            case "DIRECTORA":
                c.setFirmadoPorDirectora(true);
                break;
        }

        if (c.estanTodosFirmantes()) {
            c.setEstado("VIGENTE");
        } else {
            c.setEstado("FIRMADO");
        }

        contratos.put(idContrato, c);
    }

    public List<Contrato> findContratosPendientesDeFirma() {
        return contratos.values().stream()
                .filter(c -> !c.estanTodosFirmantes())
                .collect(Collectors.toList());
    }

    // ===== CRUD MATRICULA CON GESTIÓN DE CUPOS =====

    /**
     * Verifica si hay cupo disponible para el grado y jornada
     */
    public boolean verificarDisponibilidadCupo(String grado) {
        return factoryCupos.findCupoDisponibleByGradoYJornada(grado).isPresent();
    }

    /**
     * Obtiene información de disponibilidad de cupos
     */
    public String obtenerInfoDisponibilidad(String grado) {
        Optional<Cupo> cupoOpt = factoryCupos.findCupoDisponibleByGradoYJornada(grado);
        if (!cupoOpt.isPresent()) {
            return "No hay cupos disponibles para " + grado;
        }
        Cupo cupo = cupoOpt.get();
        int disponibles = cupo.getCuposDisponibles() - cupo.getCuposOcupados();
        return "Cupos disponibles: " + disponibles + " de " + cupo.getCuposDisponibles();
    }

    public Matricula createMatricula(Estudiante estudiante, Contrato contrato, double valorMatricula) {
        // Validar que el contrato esté firmado
        if (contrato == null || !contrato.estanTodosFirmantes()) {
            throw new IllegalArgumentException("El contrato debe estar completamente firmado");
        }

        // Validar que el estudiante tenga grado asignado
        if (estudiante.getGrado() == null || estudiante.getGrado().isEmpty()) {
            throw new IllegalArgumentException("El estudiante debe tener un grado asignado");
        }

        // Obtener grado y jornada del estudiante
        String grado = estudiante.getGrado();


        // VALIDAR DISPONIBILIDAD DE CUPO
        if (!verificarDisponibilidadCupo(grado)) {
            throw new IllegalArgumentException(
                    "No hay cupos disponibles para " + grado +". " +
                            "Por favor, contacte a la administración o seleccione otra jornada."
            );
        }

        // Crear la matrícula (el cupo se asignará cuando sea aprobada)
        long id = idGenMatricula.getAndIncrement();
        Matricula m = new Matricula(id, estudiante, contrato, LocalDate.now(), valorMatricula);
        matriculas.put(id, m);
        return m;
    }

    public Optional<Matricula> findMatriculaById(long id) {
        return Optional.ofNullable(matriculas.get(id));
    }

    public List<Matricula> listMatriculas() {
        return new ArrayList<>(matriculas.values());
    }

    public List<Matricula> listMatriculasPendientesAprobacion() {
        return matriculas.values().stream()
                .filter(m -> "PENDIENTE".equals(m.getEstado()))
                .collect(Collectors.toList());
    }

    public void updateMatricula(Matricula m) {
        if (!matriculas.containsKey(m.getIdMatricula()))
            throw new NoSuchElementException("Matrícula no existe: " + m.getIdMatricula());
        matriculas.put(m.getIdMatricula(), m);
    }

    /**
     * Aprueba la matrícula y ASIGNA automáticamente el cupo
     */
    public void aprobarMatricula(long idMatricula, Directora directora) {
        Matricula m = matriculas.get(idMatricula);
        if (m == null) throw new NoSuchElementException("Matrícula no existe");

        if (!"PENDIENTE".equals(m.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar matrículas PENDIENTES");
        }

        Estudiante estudiante = m.getEstudiante();
        String grado = estudiante.getGrado();


        // BUSCAR CUPO DISPONIBLE
        Optional<Cupo> cupoDisponible = factoryCupos.findCupoDisponibleByGradoYJornada(grado);

        if (!cupoDisponible.isPresent()) {
            throw new IllegalStateException(
                    "No hay cupos disponibles para " + grado +  ". " +
                            "No se puede aprobar la matrícula."
            );
        }

        Cupo cupo = cupoDisponible.get();

        // OCUPAR EL CUPO
        cupo.ocuparCupo();
        factoryCupos.updateCupo(cupo);

        // ASIGNAR CUPO A LA MATRÍCULA
        m.setCupoAsignado(cupo);
        m.setEstado("APROBADA");
        m.setDirectoraAprobadora(directora);
        m.setFechaAprobacion(LocalDate.now());

        // Actualizar estado del estudiante
        estudiante.setEstadoMatricula("APROBADA");

        matriculas.put(idMatricula, m);
    }

    /**
     * Rechaza la matrícula (NO se asigna cupo)
     */
    public void rechazarMatricula(long idMatricula, String motivo) {
        Matricula m = matriculas.get(idMatricula);
        if (m == null) throw new NoSuchElementException("Matrícula no existe");

        if (!"PENDIENTE".equals(m.getEstado())) {
            throw new IllegalStateException("Solo se pueden rechazar matrículas PENDIENTES");
        }

        m.setEstado("RECHAZADA");
        m.setDescripcion("Rechazo: " + motivo);

        // Actualizar estado del estudiante
        if (m.getEstudiante() != null) {
            m.getEstudiante().setEstadoMatricula("RECHAZADA");
        }

        matriculas.put(idMatricula, m);
    }

    /**
     * Confirma el pago y activa la matrícula (el cupo ya fue asignado)
     */
    public void confirmarPagoMatricula(long idMatricula) {
        Matricula m = matriculas.get(idMatricula);
        if (m == null) throw new NoSuchElementException("Matrícula no existe");

        if (!"APROBADA".equals(m.getEstado())) {
            throw new IllegalArgumentException(
                    "La matrícula debe estar APROBADA antes de confirmar pago. Estado actual: " + m.getEstado()
            );
        }

        m.setEstado("ACTIVA");
        m.setPagoPendiente(false);

        // Actualizar estado del estudiante
        if (m.getEstudiante() != null) {
            m.getEstudiante().setEstadoMatricula("ACTIVA");
        }

        matriculas.put(idMatricula, m);
    }

    /**
     * Elimina la matrícula y LIBERA el cupo si estaba asignado
     */
    public void deleteMatricula(long id) {
        Matricula m = matriculas.get(id);
        if (m != null) {
            // LIBERAR EL CUPO si fue asignado
            if (m.getCupoAsignado() != null) {
                Cupo cupo = m.getCupoAsignado();
                cupo.liberarCupo();
                factoryCupos.updateCupo(cupo);
            }

            // Actualizar estado del estudiante
            if (m.getEstudiante() != null) {
                m.getEstudiante().setEstadoMatricula("PENDIENTE");
            }
        }
        matriculas.remove(id);
    }

    public List<Matricula> findMatriculasByEstudianteId(long estudianteId) {
        return matriculas.values().stream()
                .filter(m -> m.getEstudiante() != null && m.getEstudiante().getId() == estudianteId)
                .collect(Collectors.toList());
    }

    public List<Matricula> findMatriculasActivas() {
        return matriculas.values().stream()
                .filter(m -> "ACTIVA".equals(m.getEstado()))
                .collect(Collectors.toList());
    }

    public Map<Long, Matricula> getMatriculas() {
        return new HashMap<>(matriculas);
    }

    // Método auxiliar para estadísticas
    public Map<String, Integer> obtenerEstadisticasCupos() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total Matrículas", matriculas.size());
        stats.put("Pendientes", (int) matriculas.values().stream()
                .filter(m -> "PENDIENTE".equals(m.getEstado())).count());
        stats.put("Aprobadas", (int) matriculas.values().stream()
                .filter(m -> "APROBADA".equals(m.getEstado())).count());
        stats.put("Activas", (int) matriculas.values().stream()
                .filter(m -> "ACTIVA".equals(m.getEstado())).count());
        stats.put("Rechazadas", (int) matriculas.values().stream()
                .filter(m -> "RECHAZADA".equals(m.getEstado())).count());
        return stats;
    }
}