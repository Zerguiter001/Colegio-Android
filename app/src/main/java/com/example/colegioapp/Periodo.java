package com.example.colegioapp;

public class Periodo {
    private int ID_PERIODO;
    private String NOMBRE;
    private int ORDEN;
    private String FECHA_INICIO;
    private String FECHA_FIN;

    // Getters y setters
    public int getID_PERIODO() {
        return ID_PERIODO;
    }

    public void setID_PERIODO(int ID_PERIODO) {
        this.ID_PERIODO = ID_PERIODO;
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