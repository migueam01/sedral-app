package com.mam.alicamp.db.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.mam.alicamp.constantes.Constantes;

import java.util.Objects;

@Entity(tableName = Constantes.NOMBRE_TABLA_RESPONSABLE)
public class Responsable {
    @PrimaryKey
    @ColumnInfo(name = "id_responsable")
    private Integer idResponsable;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private boolean sincronizado;
    private boolean eliminado;

    public Responsable() {
    }

    public Responsable(String nombre, String apellido, String telefono, String email,
                       boolean sincronizado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.sincronizado = sincronizado;
        this.eliminado = false;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer id) {
        this.idResponsable = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (!(o instanceof Responsable)) return false;
        Responsable r = (Responsable) o;
        return Objects.equals(idResponsable, r.idResponsable) &&
                Objects.equals(nombre, r.nombre) &&
                Objects.equals(apellido, r.apellido) &&
                Objects.equals(telefono, r.telefono) &&
                Objects.equals(email, r.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idResponsable, nombre, apellido, telefono, email);
    }

    @NonNull
    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
