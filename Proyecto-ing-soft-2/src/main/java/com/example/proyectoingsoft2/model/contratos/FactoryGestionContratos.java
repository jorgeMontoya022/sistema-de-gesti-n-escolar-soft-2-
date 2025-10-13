package com.example.proyectoingsoft2.model.contratos;

import com.example.proyectoingsoft2.model.personas.Directora;
import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Acudiente;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Manager en memoria para Contratos y Matrículas
 */
public class FactoryGestionContratos {
    private final AtomicLong idGenContrato = new AtomicLong(1);
    private final AtomicLong idGenMatricula = new AtomicLong(1);

    private final Map<Long, Contrato> contratos = new LinkedHashMap<>();
    private final Map<Long, Matricula> matriculas = new LinkedHashMap<>();

    // Contrato CRUD
    public Contrato createContrato(LocalDate fechaEmision, LocalDate fechaVencimiento, String detalleCondiciones, Acudiente acudiente) {
        long id = idGenContrato.getAndIncrement();
        Contrato c = new Contrato(id, fechaEmision, fechaVencimiento, detalleCondiciones, acudiente);
        contratos.put(id, c);
        return c;
    }

    public Optional<Contrato> findContratoById(long id) { return Optional.ofNullable(contratos.get(id)); }
    public List<Contrato> listContratos() { return new ArrayList<>(contratos.values()); }

    public void updateContrato(Contrato c) {
        if (!contratos.containsKey(c.getIdContrato())) throw new NoSuchElementException("Contrato no existe: " + c.getIdContrato());
        contratos.put(c.getIdContrato(), c);
    }

    public void deleteContrato(long id) { contratos.remove(id); }

    // Matricula CRUD
    public Matricula createMatricula(LocalDate fechaRegistro, String estado, double valorMatricula,
                                     Estudiante estudiante, Contrato contrato, Directora directora) {
        long id = idGenMatricula.getAndIncrement();
        Matricula m = new Matricula(id, fechaRegistro, estado, valorMatricula, estudiante, contrato, directora);
        matriculas.put(id, m);
        return m;
    }

    public Optional<Matricula> findMatriculaById(long id) { return Optional.ofNullable(matriculas.get(id)); }
    public List<Matricula> listMatriculas() { return new ArrayList<>(matriculas.values()); }

    public void updateMatricula(Matricula m) {
        if (!matriculas.containsKey(m.getIdMatricula())) throw new NoSuchElementException("Matricula no existe: " + m.getIdMatricula());
        matriculas.put(m.getIdMatricula(), m);
    }

    public void deleteMatricula(long id) { matriculas.remove(id); }

    // busquedas
    public List<Matricula> findMatriculasByEstudianteId(long estudianteId) {
        return matriculas.values().stream()
                .filter(m -> m.getEstudiante() != null && m.getEstudiante().getId() == estudianteId)
                .collect(Collectors.toList());
    }
}
