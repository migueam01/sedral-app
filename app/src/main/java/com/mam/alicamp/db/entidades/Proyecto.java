package com.mam.alicamp.db.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

import com.mam.alicamp.constantes.Constantes;

import java.util.Objects;

@Entity(tableName = Constantes.NOMBRE_TABLA_PROYECTOS,
        indices = {
                @Index("fk_id_gadm")
        },
        foreignKeys = @ForeignKey(entity = Gadm.class,
                parentColumns = "id_gadm",
                childColumns = "fk_id_gadm",
                onDelete = CASCADE))
public class Proyecto {
    @PrimaryKey
    @ColumnInfo(name = "id_proyecto")
    private Integer idProyecto;
    private String nombre;
    private String alias;
    private double dotacion;
    private int poblacion;
    @ColumnInfo(name = "fk_id_gadm")
    private Integer idGadm;

    @Ignore
    public Proyecto() {
    }

    public Proyecto(String nombre, String alias, Integer idGadm, double dotacion, int poblacion) {
        this.nombre = nombre;
        this.alias = alias;
        this.dotacion = dotacion;
        this.poblacion = poblacion;
        this.idGadm = idGadm;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer id) {
        this.idProyecto = id;
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

    public double getDotacion() {
        return dotacion;
    }

    public void setDotacion(double dotacion) {
        this.dotacion = dotacion;
    }

    public int getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(int poblacion) {
        this.poblacion = poblacion;
    }

    public Integer getIdGadm() {
        return idGadm;
    }

    public void setIdGadm(Integer idGadm) {
        this.idGadm = idGadm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proyecto)) return false;
        Proyecto p = (Proyecto) o;
        return Objects.equals(idProyecto, p.idProyecto) &&
                Objects.equals(nombre, p.nombre) &&
                Objects.equals(alias, p.alias) &&
                Objects.equals(dotacion, p.dotacion) &&
                Objects.equals(poblacion, p.poblacion) &&
                Objects.equals(idGadm, p.idGadm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProyecto, nombre, alias, dotacion, poblacion, idGadm);
    }

    @NonNull
    @Override
    public String toString() {
        return alias;
    }
}
