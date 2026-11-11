package com.uniajc.modelo;

import java.sql.Timestamp;

public class Usuario {
    private int id;
    private String username;
    private String password;
    private String nombre;
    private String rol;
    private boolean activo;
    private Timestamp fechaCreacion;

    public Usuario() {
        this.activo = true;
    }

    public Usuario(int id, String username, String password, String nombre, String rol, boolean activo, Timestamp fechaCreacion) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Usuario(String username, String password, String nombre, String rol, boolean activo) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}

