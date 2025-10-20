package com.example.proyectoingsoft2.model.cupos;

import java.util.Objects;

public class Cupo {
    private long id;
    private String grado; // Pre-jardín a Quinto
    private double valor;
    private String estado; // DISPONIBLE, RESERVADO, OCUPADO
    private String jornada; // MAÑANA, TARDE, COMPLETA
    private int cuposDisponibles; // Número de cupos disponibles
    private int cuposOcupados; // Número de cupos ocupados

    public Cupo() {}

    public Cupo(long id, String grado, double valor, String estado, String jornada, int cuposDisponibles) {
        this.id = id;
        this.grado = grado;
        this.valor = valor;
        this.estado = estado;
        this.jornada = jornada;
        this.cuposDisponibles = cuposDisponibles;
        this.cuposOcupados = 0;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }

    public int getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(int cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }

    public int getCuposOcupados() { return cuposOcupados; }
    public void setCuposOcupados(int cuposOcupados) { this.cuposOcupados = cuposOcupados; }

    public boolean hayDisponibilidad() {
        return cuposDisponibles > cuposOcupados && "DISPONIBLE".equals(estado);
    }

    public void ocuparCupo() {
        if (hayDisponibilidad()) {
            cuposOcupados++;
            if (cuposOcupados >= cuposDisponibles) {
                estado = "OCUPADO";
            }
        }
    }

    public void liberarCupo() {
        if (cuposOcupados > 0) {
            cuposOcupados--;
            estado = "DISPONIBLE";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cupo)) return false;
        Cupo cupo = (Cupo) o;
        return id == cupo.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Cupo{" + "Grado=" + grado + ", Jornada=" + jornada +
                ", Disponibles=" + cuposDisponibles + ", Estado=" + estado + "}";
    }
}