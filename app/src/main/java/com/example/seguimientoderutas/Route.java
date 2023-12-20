package com.example.seguimientoderutas;

public class Route {
    private String puntoPartida;
    private String puntoLlegada;
    private String fechaHora;

    // Constructor vacío requerido por Firebase
    public Route() {
    }

    public Route(String puntoPartida, String puntoLlegada, String fechaHora) {
        this.puntoPartida = puntoPartida;
        this.puntoLlegada = puntoLlegada;
        this.fechaHora = fechaHora;
    }

    public String getPuntoPartida() {
        return puntoPartida;
    }

    public void setPuntoPartida(String puntoPartida) {
        this.puntoPartida = puntoPartida;
    }

    public String getPuntoLlegada() {
        return puntoLlegada;
    }

    public void setPuntoLlegada(String puntoLlegada) {
        this.puntoLlegada = puntoLlegada;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}

