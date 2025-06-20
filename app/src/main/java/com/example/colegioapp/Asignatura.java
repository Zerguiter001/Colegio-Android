package com.example.colegioapp;

public class Asignatura {
    private int ID_ASIGNATURA;
    private String NOMBRE;

    // Getters y setters
    public int getID_ASIGNATURA() {
        return ID_ASIGNATURA;
    }

    public void setID_ASIGNATURA(int ID_ASIGNATURA) {
        this.ID_ASIGNATURA = ID_ASIGNATURA;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    @Override
    public String toString() {
        return NOMBRE;
    }
}