package com.mam.alicamp.db.dto;

import java.io.Serializable;

public class PozoDimensiones implements Serializable {
    private double dimensionTapa;
    private double alturaPozo;
    private double ancho;
    private String fluido;
    private String estadoPozo;
    private int actividadCompletada;

    public double getDimensionTapa() {
        return dimensionTapa;
    }

    public void setDimensionTapa(double dimensionTapa) {
        this.dimensionTapa = dimensionTapa;
    }

    public double getAlturaPozo() {
        return alturaPozo;
    }

    public void setAlturaPozo(double alturaPozo) {
        this.alturaPozo = alturaPozo;
    }

    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

    public String getFluido() {
        return fluido;
    }

    public void setFluido(String fluido) {
        this.fluido = fluido;
    }

    public String getEstadoPozo() {
        return estadoPozo;
    }

    public void setEstadoPozo(String estadoPozo) {
        this.estadoPozo = estadoPozo;
    }

    public int getActividadCompletada() {
        return actividadCompletada;
    }

    public void setActividadCompletada(int actividadCompletada) {
        this.actividadCompletada = actividadCompletada;
    }
}
