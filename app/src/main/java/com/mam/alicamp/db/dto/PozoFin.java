package com.mam.alicamp.db.dto;

import java.io.Serializable;

public class PozoFin implements Serializable {
    private String observacion;
    private int actividadCompletada;

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getActividadCompletada() {
        return actividadCompletada;
    }

    public void setActividadCompletada(int actividadCompletada) {
        this.actividadCompletada = actividadCompletada;
    }
}
