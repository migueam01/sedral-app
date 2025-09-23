package com.mam.alicamp.db.dto;

import com.mam.alicamp.db.entidades.Pozo;

import java.io.Serializable;


public class TuberiaSpring implements Serializable {
    private Integer idTuberia;
    private String orientacion;
    private double base;
    private double corona;
    private int diametro;
    private String material;
    private String flujo;
    private String funciona;
    private boolean sincronizado;
    private Pozo pozoInicio;
    private Pozo pozoFin;

    public Integer getIdTuberia() {
        return idTuberia;
    }

    public void setIdTuberia(Integer idTuberia) {
        this.idTuberia = idTuberia;
    }

    public String getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(String orientacion) {
        this.orientacion = orientacion;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getCorona() {
        return corona;
    }

    public void setCorona(double corona) {
        this.corona = corona;
    }

    public int getDiametro() {
        return diametro;
    }

    public void setDiametro(int diametro) {
        this.diametro = diametro;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getFlujo() {
        return flujo;
    }

    public void setFlujo(String flujo) {
        this.flujo = flujo;
    }

    public String getFunciona() {
        return funciona;
    }

    public void setFunciona(String funciona) {
        this.funciona = funciona;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Pozo getPozoInicio() {
        return pozoInicio;
    }

    public void setPozoInicio(Pozo pozoInicio) {
        this.pozoInicio = pozoInicio;
    }

    public Pozo getPozoFin() {
        return pozoFin;
    }

    public void setPozoFin(Pozo pozoFin) {
        this.pozoFin = pozoFin;
    }
}
