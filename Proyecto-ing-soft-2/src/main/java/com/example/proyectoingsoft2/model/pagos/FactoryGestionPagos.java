package com.example.proyectoingsoft2.model.pagos;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manager en memoria para pagos.
 */
public class FactoryGestionPagos {
    private final AtomicLong idGenPago = new AtomicLong(1);
    private final Map<Long, Pago> pagos = new LinkedHashMap<>();

    public Pago createPago(double monto, LocalDate fechaPago, String estado, String metodoPago, String comprobante, String descripcion, String referenciaTipo, long referenciaId) {
        long id = idGenPago.getAndIncrement();
        Pago p = new Pago(id, monto, fechaPago, estado, metodoPago, comprobante, descripcion);
        p.setReferenciaTipo(referenciaTipo);
        p.setReferenciaId(referenciaId);
        pagos.put(id, p);
        return p;
    }

    public Optional<Pago> findPagoById(long id) { return Optional.ofNullable(pagos.get(id)); }
    public List<Pago> listPagos() { return new ArrayList<>(pagos.values()); }

    public void updatePago(Pago p) {
        if (!pagos.containsKey(p.getIdPago())) throw new NoSuchElementException("Pago no existe: " + p.getIdPago());
        pagos.put(p.getIdPago(), p);
    }

    public void deletePago(long id) { pagos.remove(id); }

    public List<Pago> findPagosByReferencia(String tipo, long referenciaId) {
        List<Pago> results = new ArrayList<>();
        for (Pago p : pagos.values()) {
            if (Objects.equals(p.getReferenciaTipo(), tipo) && p.getReferenciaId() == referenciaId) {
                results.add(p);
            }
        }
        return results;
    }
}
