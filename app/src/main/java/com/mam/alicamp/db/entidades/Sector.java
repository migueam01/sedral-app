package com.mam.alicamp.db.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

import com.mam.alicamp.constantes.Constantes;

import java.util.Objects;

@Entity(tableName = Constantes.NOMBRE_TABLA_SECTORES,
        indices = {
                @Index("fk_id_proyecto")
        },
        foreignKeys = @ForeignKey(entity = Proyecto.class,
                parentColumns = "id_proyecto",
                childColumns = "fk_id_proyecto",
                onDelete = CASCADE))
public class Sector {
    @PrimaryKey
    @ColumnInfo(name = "id_sector")
    private Integer idSector;
    private String nombre;
    @ColumnInfo(name = "fk_id_proyecto")
    private Integer idProyecto;

    public Sector() {
    }

    public Sector(String nombre, Integer idProyecto) {
        this.nombre = nombre;
        this.idProyecto = idProyecto;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer id) {
        this.idSector = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sector)) return false;
        Sector s = (Sector) o;
        return Objects.equals(idSector, s.idSector) &&
                Objects.equals(nombre, s.nombre) &&
                Objects.equals(idProyecto, s.idProyecto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSector, nombre, idProyecto);
    }

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
