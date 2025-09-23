package com.mam.alicamp.db.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

import com.mam.alicamp.constantes.Constantes;

import java.util.Objects;

@Entity(tableName = Constantes.NOMBRE_TABLA_TUBERIAS,
        indices = {
                @Index("fk_pozo_inicio"),
                @Index("fk_pozo_fin")
        },
        foreignKeys = {
                @ForeignKey(entity = Pozo.class,
                        parentColumns = "id_pozo",
                        childColumns = "fk_pozo_inicio",
                        onDelete = CASCADE),
                @ForeignKey(entity = Pozo.class,
                        parentColumns = "id_pozo",
                        childColumns = "fk_pozo_fin",
                        onDelete = CASCADE)
        })
public class Tuberia {
    @PrimaryKey
    @ColumnInfo(name = "id_tuberia")
    private Integer idTuberia;
    private String orientacion;
    private double base;
    private double corona;
    private int diametro;
    private String material;
    private String flujo;
    private String funciona;
    private boolean sincronizado;
    private boolean eliminado;
    @ColumnInfo(name = "fk_pozo_inicio")
    private Integer idPozoInicio;
    @ColumnInfo(name = "fk_pozo_fin")
    private Integer idPozoFin;

    @Ignore
    public Tuberia() {
    }

    public Tuberia(String orientacion, double base, double corona, int diametro, String material,
                   String flujo, String funciona, Integer idPozoInicio, Integer idPozoFin, boolean sincronizado) {
        this.orientacion = orientacion;
        this.base = base;
        this.corona = corona;
        this.diametro = diametro;
        this.material = material;
        this.flujo = flujo;
        this.funciona = funciona;
        this.idPozoInicio = idPozoInicio;
        this.idPozoFin = idPozoFin;
        this.sincronizado = sincronizado;
        this.eliminado = false;
    }

    public Integer getIdTuberia() {
        return idTuberia;
    }

    public void setIdTuberia(Integer id) {
        this.idTuberia = id;
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

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Integer getIdPozoInicio() {
        return idPozoInicio;
    }

    public void setIdPozoInicio(Integer idPozoInicio) {
        this.idPozoInicio = idPozoInicio;
    }

    public Integer getIdPozoFin() {
        return idPozoFin;
    }

    public void setIdPozoFin(Integer idPozoFin) {
        this.idPozoFin = idPozoFin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuberia)) return false;
        Tuberia t = (Tuberia) o;
        return Objects.equals(idTuberia, t.idTuberia) &&
                Objects.equals(orientacion, t.orientacion) &&
                Objects.equals(base, t.base) &&
                Objects.equals(corona, t.corona) &&
                Objects.equals(diametro, t.diametro) &&
                Objects.equals(material, t.material) &&
                Objects.equals(flujo, t.flujo) &&
                Objects.equals(funciona, t.funciona) &&
                Objects.equals(idPozoInicio, t.idPozoInicio) &&
                Objects.equals(idPozoFin, t.idPozoFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTuberia, orientacion, base, corona, diametro, material, flujo,
                funciona, idPozoInicio, idPozoFin);
    }
}
