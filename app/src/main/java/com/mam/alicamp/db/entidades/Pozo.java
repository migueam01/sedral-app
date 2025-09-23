package com.mam.alicamp.db.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.mam.alicamp.constantes.Constantes;

@Entity(tableName = Constantes.NOMBRE_TABLA_POZOS,
        indices = {
                @Index("fk_id_sector"),
                @Index("fk_id_responsable"),
                @Index("fk_id_descarga"),
                @Index(value = "nombre", unique = true)
        },
        foreignKeys = {
                @ForeignKey(entity = Sector.class,
                        parentColumns = "id_sector",
                        childColumns = "fk_id_sector",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Responsable.class,
                        parentColumns = "id_responsable",
                        childColumns = "fk_id_responsable",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Descarga.class,
                        parentColumns = "id_descarga",
                        childColumns = "fk_id_descarga",
                        onDelete = ForeignKey.CASCADE)
        })
public class Pozo {
    @PrimaryKey
    @ColumnInfo(name = "id_pozo")
    private Integer idPozo;
    private String nombre;
    private String sistema;
    private String fechaCatastro;
    private String fechaActualizacion;
    private String tapado;
    private double coordNorte;
    private double coordEste;
    private double cota;
    private double aproximacion;
    private String zona;
    private int srid;
    private String estado;
    private String calzada;
    private String fluido;
    private double dimensionTapa;
    private double altura;
    private double ancho;
    private String calleOE;
    private String calleNS;
    private String observacion;
    private String pathMedia;
    private boolean sincronizado;
    private int actividadCompletada;
    private boolean eliminado;
    @ColumnInfo(name = "fk_id_sector")
    private Integer idSector;
    @ColumnInfo(name = "fk_id_responsable")
    private Integer idResponsable;
    @ColumnInfo(name = "fk_id_descarga")
    private Integer idDescarga;

    public Pozo(@NonNull String nombre, boolean sincronizado, String fechaCatastro, String fechaActualizacion,
                String tapado, String sistema, String pathMedia, int actividadCompletada,
                Integer idSector, Integer idResponsable, Integer idDescarga) {
        this.nombre = nombre;
        this.fechaCatastro = fechaCatastro;
        this.fechaActualizacion = fechaActualizacion;
        this.sincronizado = sincronizado;
        this.tapado = tapado;
        this.sistema = sistema;
        this.pathMedia = pathMedia;
        this.actividadCompletada = actividadCompletada;
        this.eliminado = false;
        this.idSector = idSector;
        this.idResponsable = idResponsable;
        this.idDescarga = idDescarga;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public Integer getIdPozo() {
        return idPozo;
    }

    public void setIdPozo(Integer idPozo) {
        this.idPozo = idPozo;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
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

    public String getTapado() {
        return tapado;
    }

    public void setTapado(String tapado) {
        this.tapado = tapado;
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

    public double getAproximacion() {
        return aproximacion;
    }

    public void setAproximacion(double aproximacion) {
        this.aproximacion = aproximacion;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCalzada() {
        return calzada;
    }

    public void setCalzada(String calzada) {
        this.calzada = calzada;
    }

    public String getFluido() {
        return fluido;
    }

    public void setFluido(String fluido) {
        this.fluido = fluido;
    }

    public double getDimensionTapa() {
        return dimensionTapa;
    }

    public void setDimensionTapa(double dimensionTapa) {
        this.dimensionTapa = dimensionTapa;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getPathMedia() {
        return pathMedia;
    }

    public void setPathMedia(String pathMedia) {
        this.pathMedia = pathMedia;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getActividadCompletada() {
        return actividadCompletada;
    }

    public void setActividadCompletada(int actividadCompletada) {
        this.actividadCompletada = actividadCompletada;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
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
    }//</editor-fold>

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
