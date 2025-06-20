package com.example.colegioapp;

public class NotaRequest {
    private int idUsuarioDocente;
    private int idUsuarioAlumno;
    private int idAsignatura;
    private int idPeriodo;
    private int idRubrica;
    private float nota;

    // Constructor
    public NotaRequest(int idUsuarioDocente, int idUsuarioAlumno, int idAsignatura, int idPeriodo, int idRubrica, float nota) {
        this.idUsuarioDocente = idUsuarioDocente;
        this.idUsuarioAlumno = idUsuarioAlumno;
        this.idAsignatura = idAsignatura;
        this.idPeriodo = idPeriodo;
        this.idRubrica = idRubrica;
        this.nota = nota;
    }

    // Getters y setters
    public int getIdUsuarioDocente() {
        return idUsuarioDocente;
    }

    public void setIdUsuarioDocente(int idUsuarioDocente) {
        this.idUsuarioDocente = idUsuarioDocente;
    }

    public int getIdUsuarioAlumno() {
        return idUsuarioAlumno;
    }

    public void setIdUsuarioAlumno(int idUsuarioAlumno) {
        this.idUsuarioAlumno = idUsuarioAlumno;
    }

    public int getIdAsignatura() {
        return idAsignatura;
    }

    public void setIdAsignatura(int idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public int getIdRubrica() {
        return idRubrica;
    }

    public void setIdRubrica(int idRubrica) {
        this.idRubrica = idRubrica;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }
}