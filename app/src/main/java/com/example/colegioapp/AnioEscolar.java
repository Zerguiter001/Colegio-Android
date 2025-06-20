package com.example.colegioapp;

public class AnioEscolar {
    private int ID_ANIO_ESCOLAR;
    private int ANIO;
    private String FECHA_INICIO;
    private String FECHA_FIN;

    // Getters y setters
    public int getID_ANIO_ESCOLAR() {
        return ID_ANIO_ESCOLAR;
    }

    public void setID_ANIO_ESCOLAR(int ID_ANIO_ESCOLAR) {
        this.ID_ANIO_ESCOLAR = ID_ANIO_ESCOLAR;
    }

    public int getANIO() {
        return ANIO;
    }

    public void setANIO(int ANIO) {
        this.ANIO = ANIO;
    }

    @Override
    public String toString() {
        return String.valueOf(ANIO);
    }
}