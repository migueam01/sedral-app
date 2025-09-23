package com.mam.alicamp.db.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mam.alicamp.constantes.Constantes;

import java.util.Objects;

@Entity(tableName = Constantes.NOMBRE_TABLA_GADM)
public class Gadm {
    @PrimaryKey
    @ColumnInfo(name = "id_gadm")
    private Integer idGadm;
    private String nombre;
    private String alias;
    private boolean sincronizado;
    private boolean eliminado;

    @Ignore
    public Gadm() {
    }

    public Gadm(String nombre, String alias, boolean sincronizado) {
        this.nombre = nombre;
        this.alias = alias;
        this.sincronizado = sincronizado;
        this.eliminado = false;
    }

    public Integer getIdGadm() {
        return idGadm;
    }

    public void setIdGadm(Integer id) {
        this.idGadm = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
        if (!(o instanceof Gadm)) return false;
        Gadm g = (Gadm) o;
        return Objects.equals(idGadm, g.idGadm) &&
                Objects.equals(nombre, g.nombre) &&
                Objects.equals(alias, g.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGadm, nombre, alias);
    }

    @NonNull
    @Override
    public String toString() {
        return alias;
    }
}
