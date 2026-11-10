package com.uniajc.modelo;

public class Docente {
    private int id;
    private String name;
    private String especialidad;

    public Docente() {}

    public Docente(int id, String name, String especialidad) {
        this.id = id;
        this.name = name;
        this.especialidad = especialidad;
    }

    public Docente(String name, String especialidad) {
        this.name = name;
        this.especialidad = especialidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    @Override
    public String toString() {
        return "Docente{id=" + id + ", name='" + name + "', especialidad='" + especialidad + "'}";
    }
}
