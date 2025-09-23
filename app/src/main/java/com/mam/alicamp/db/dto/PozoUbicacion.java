package com.mam.alicamp.db.dto;

import java.io.Serializable;

public class PozoUbicacion implements Serializable {
    private String calleOE;
    private String calleNS;
    private double coordNorte;
    private double coordEste;
    private double cota;
    private String zona;
    private int srid;
    private double aproximacion;
    private String calzada;
    private int actividadCompletada;

    public String getCalleOE() {
        return calleOE;
    }

    public void setCalleOE(String calleOE) {
        this.calleOE = calleOE;
    }

    public String getCalleNS() {
        return calleNS;
    }

    public void setCalleNS(String calleNS) {
        this.calleNS = calleNS;
    }

    public double getCoordNorte() {
        return coordNorte;
    }

    public void setCoordNorte(double coordNorte) {
        this.coordNorte = coordNorte;
    }

    public double getCoordEste() {
        return coordEste;
    }

    public void setCoordEste(double coordEste) {
        this.coordEste = coordEste;
    }

    public double getCota() {
        return cota;
    }

    public void setCota(double cota) {
        this.cota = cota;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public double getAproximacion() {
        return aproximacion;
    }

    public void setAproximacion(double aproximacion) {
        this.aproximacion = aproximacion;
    }

    public String getCalzada() {
        return calzada;
    }

    public void setCalzada(String calzada) {
        this.calzada = calzada;
    }

    public int getActividadCompletada() {
        return actividadCompletada;
    }

    public void setActividadCompletada(int actividadCompletada) {
        this.actividadCompletada = actividadCompletada;
    }
}
