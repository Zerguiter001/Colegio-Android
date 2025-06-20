package com.example.colegioapp;

public class Rubrica {
    private int ID_RUBRICA;
    private String NOMBRE;
    private String DESCRIPCION;

    // Getters y setters
    public int getID_RUBRICA() {
        return ID_RUBRICA;
    }

    public void setID_RUBRICA(int ID_RUBRICA) {
        this.ID_RUBRICA = ID_RUBRICA;
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