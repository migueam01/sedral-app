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
    private String username;
    private String telefono;
    private String correo;

    public Responsable() {
    }

    public Responsable(String nombre, String apellido, String username, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.telefono = telefono;
        this.correo = correo;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Responsable)) return false;
        Responsable r = (Responsable) o;
        return Objects.equals(idResponsable, r.idResponsable) &&
                Objects.equals(nombre, r.nombre) &&
                Objects.equals(apellido, r.apellido) &&
                Objects.equals(username, r.username) &&
                Objects.equals(telefono, r.telefono) &&
                Objects.equals(correo, r.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idResponsable, nombre, apellido, username, telefono, correo);
    }

    @NonNull
    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
