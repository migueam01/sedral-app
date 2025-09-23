package com.mam.alicamp.db.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mam.alicamp.constantes.Constantes;

import java.util.Objects;

@Entity(tableName = Constantes.NOMBRE_TABLA_DESCARGAS)
public class Descarga {
    @PrimaryKey
    @ColumnInfo(name = "id_descarga")
    private Integer idDescarga;
    private String nombre;
    private String ubicacion;
    private boolean sincronizado;
    private boolean eliminado;

    @Ignore
    public Descarga() {
    }

    public Descarga(String nombre, String ubicacion, boolean sincronizado) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.sincronizado = sincronizado;
        this.eliminado = false;
    }

    public Integer getIdDescarga() {
        return idDescarga;
    }

    public void setIdDescarga(Integer id) {
        this.idDescarga = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Descarga)) return false;
        Descarga d = (Descarga) o;
        return Objects.equals(idDescarga, d.idDescarga) &&
                Objects.equals(nombre, d.nombre) &&
                Objects.equals(ubicacion, d.ubicacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDescarga, nombre, ubicacion);
    }

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
