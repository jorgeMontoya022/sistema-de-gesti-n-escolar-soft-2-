package com.example.proyectoingsoft2.model.cupos;

import java.util.Objects;

public class Cupo {
    private long id;

    private boolean disponible;
    private String estado;
    double valor;
    String grado;




    public Cupo(long id, String estado, double valor, String grado) {
        this.id = id;
        this.estado = estado;
        this.valor = valor;
        this.grado = grado;

    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }



    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cupo)) return false;
        Cupo cupo = (Cupo) o;
        return id == cupo.id;
    }

    public String getEstado() {
        return estado;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }




}
