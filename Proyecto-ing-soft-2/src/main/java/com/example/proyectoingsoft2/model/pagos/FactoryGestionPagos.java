package com.example.proyectoingsoft2.model.pagos;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FactoryGestionPagos {
    private final AtomicLong idGenPago = new AtomicLong(8000);
    private final Map<Long, Pago> pagos = new LinkedHashMap<>();
    private static FactoryGestionPagos instance;

    // ===== CRUD PAGO =====
    public Pago createPago(long idMatricula, double monto, String metodoPago, String descripcion) {
        long id = idGenPago.getAndIncrement();
        Pago p = new Pago(id, idMatricula, monto, metodoPago, descripcion);
        pagos.put(id, p);
        return p;
    }

    private FactoryGestionPagos() {

    }

    public static FactoryGestionPagos getInstance() {
        if (instance == null) {
            instance = new FactoryGestionPagos();
        }
        return instance;
    }

    public Optional<Pago> findPagoById(long id) {
        return Optional.ofNullable(pagos.get(id));
    }

    public List<Pago> listPagos() {
        return new ArrayList<>(pagos.values());
    }

    public List<Pago> listPagosByMatricula(long idMatricula) {
        return pagos.values().stream()
                .filter(p -> p.getIdMatricula() == idMatricula)
                .collect(Collectors.toList());
    }

    public List<Pago> listPagosByEstado(String estado) {
        return pagos.values().stream()
                .filter(p -> p.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    public List<Pago> listPagosPendientes() {
        return pagos.values().stream()
                .filter(p -> "PENDIENTE".equals(p.getEstado()))
                .collect(Collectors.toList());
    }

    public List<Pago> listPagosCompletados() {
        return pagos.values().stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .collect(Collectors.toList());
    }

    public void updatePago(Pago p) {
        if (!pagos.containsKey(p.getIdPago()))
            throw new NoSuchElementException("Pago no existe: " + p.getIdPago());
        pagos.put(p.getIdPago(), p);
    }

    public void completarPago(long idPago, String numeroTransaccion) {
        Pago p = pagos.get(idPago);
        if (p == null) throw new NoSuchElementException("Pago no existe");

        p.setEstado("COMPLETADO");
        p.setFechaPago(LocalDate.now());
        p.setNumeroTransaccion(numeroTransaccion);
        pagos.put(idPago, p);
    }

    public void rechazarPago(long idPago, String motivo) {
        Pago p = pagos.get(idPago);
        if (p == null) throw new NoSuchElementException("Pago no existe");

        p.setEstado("RECHAZADO");
        p.setDescripcion("Rechazo: " + motivo);
        pagos.put(idPago, p);
    }

    public void procesarReembolso(long idPago) {
        Pago p = pagos.get(idPago);
        if (p == null) throw new NoSuchElementException("Pago no existe");
        if (!"COMPLETADO".equals(p.getEstado()))
            throw new IllegalArgumentException("Solo se pueden reembolsar pagos completados");

        p.setEstado("REEMBOLSADO");
        p.setFechaReembolso(LocalDate.now());
        pagos.put(idPago, p);
    }

    public void deletePago(long id) {
        pagos.remove(id);
    }

    public double calcularTotalPagosCompletados() {
        return pagos.values().stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .mapToDouble(Pago::getMonto)
                .sum();
    }

    public double calcularTotalPagosPendientes() {
        return pagos.values().stream()
                .filter(p -> "PENDIENTE".equals(p.getEstado()))
                .mapToDouble(Pago::getMonto)
                .sum();
    }

    public Map<String, Double> reportePagosPorMetodo() {
        return pagos.values().stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .collect(Collectors.groupingBy(
                        Pago::getMetodoPago,
                        Collectors.summingDouble(Pago::getMonto)
                ));
    }

    public Map<Long, Pago> getPagos() {
        return new HashMap<>(pagos);
    }
}