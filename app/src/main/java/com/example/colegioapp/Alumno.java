package com.example.colegioapp;

public class Alumno {
    private int ID_USUARIO;
    private String NOMBRE_COMPLETO;

    // Getters y setters
    public int getID_USUARIO() {
        return ID_USUARIO;
    }

    public void setID_USUARIO(int ID_USUARIO) {
        this.ID_USUARIO = ID_USUARIO;
    }

    public String getNOMBRE_COMPLETO() {
        return NOMBRE_COMPLETO;
    }

    public void setNOMBRE_COMPLETO(String NOMBRE_COMPLETO) {
        this.NOMBRE_COMPLETO = NOMBRE_COMPLETO;
    }

    @Override
    public String toString() {
        return NOMBRE_COMPLETO;
    }
}