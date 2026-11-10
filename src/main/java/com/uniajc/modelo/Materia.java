package com.uniajc.modelo;

public class Materia {
    private int id;
    private String name;
    private int credits;

    public Materia() {}

    public Materia(int id, String name, int credits) {
        this.id = id;
        this.name = name;
        this.credits = credits;
    }

    public Materia(String name, int credits) {
        this.name = name;
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                '}';
    }
}

