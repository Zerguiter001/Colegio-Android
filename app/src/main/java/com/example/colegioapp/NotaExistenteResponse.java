package com.example.colegioapp;

public class NotaExistenteResponse {
    private String ASIGNATURA;
    private String RUBRICA;
    private String DESCRIPCION_RUBRICA;
    private String ALUMNO;
    private String NOTA;
    private String PROFESOR;
    private String PERIODO;
    private int ANIO_ESCOLAR;

    // Getters y setters
    public String getASIGNATURA() { return ASIGNATURA; }
    public void setASIGNATURA(String ASIGNATURA) { this.ASIGNATURA = ASIGNATURA; }
    public String getRUBRICA() { return RUBRICA; }
    public void setRUBRICA(String RUBRICA) { this.RUBRICA = RUBRICA; }
    public String getDESCRIPCION_RUBRICA() { return DESCRIPCION_RUBRICA; }
    public void setDESCRIPCION_RUBRICA(String DESCRIPCION_RUBRICA) { this.DESCRIPCION_RUBRICA = DESCRIPCION_RUBRICA; }
    public String getALUMNO() { return ALUMNO; }
    public void setALUMNO(String ALUMNO) { this.ALUMNO = ALUMNO; }
    public String getNOTA() { return NOTA; }
    public void setNOTA(String NOTA) { this.NOTA = NOTA; }
    public String getPROFESOR() { return PROFESOR; }
    public void setPROFESOR(String PROFESOR) { this.PROFESOR = PROFESOR; }
    public String getPERIODO() { return PERIODO; }
    public void setPERIODO(String PERIODO) { this.PERIODO = PERIODO; }
    public int getANIO_ESCOLAR() { return ANIO_ESCOLAR; }
    public void setANIO_ESCOLAR(int ANIO_ESCOLAR) { this.ANIO_ESCOLAR = ANIO_ESCOLAR; }

    public boolean isNotaRegistrada() {
        return NOTA != null && !NOTA.equals("No registrado");
    }

    public float getNotaValue() {
        try {
            return Float.parseFloat(NOTA);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}