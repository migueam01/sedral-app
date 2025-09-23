package com.mam.alicamp.db.dto;

import java.io.Serializable;

public class PozoCreacion implements Serializable {
    private String nombre;
    private String fechaCatastro;
    private String fechaActualizacion;
    private boolean sincronizado;
    private String tapado;
    private String sistema;
    private String pathMedia;
    private int actividadCompletada;
    private Integer idSector;
    private Integer idResponsable;
    private Integer idDescarga;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaCatastro() {
        return fechaCatastro;
    }

    public void setFechaCatastro(String fechaCatastro) {
        this.fechaCatastro = fechaCatastro;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getTapado() {
        return tapado;
    }

    public void setTapado(String tapado) {
        this.tapado = tapado;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getPathMedia() {
        return pathMedia;
    }

    public void setPathMedia(String pathMedia) {
        this.pathMedia = pathMedia;
    }

    public int getActividadCompletada() {
        return actividadCompletada;
    }

    public void setActividadCompletada(int actividadCompletada) {
        this.actividadCompletada = actividadCompletada;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Integer getIdDescarga() {
        return idDescarga;
    }

    public void setIdDescarga(Integer idDescarga) {
        this.idDescarga = idDescarga;
    }
}
