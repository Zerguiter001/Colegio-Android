package com.example.colegioapp;

public class RegistroRequest {
    private String nombres;
    private String apellidos;
    private String documento;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private int idTipoPersona;
    private boolean activarAcceso;
    private int idRol;

    public RegistroRequest(String nombres, String apellidos, String documento, String fechaNacimiento,
                           String correo, String telefono, int idTipoPersona, boolean activarAcceso,
                           int idRol) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.idTipoPersona = idTipoPersona;
        this.activarAcceso = activarAcceso;
        this.idRol = idRol;
    }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public int getIdTipoPersona() { return idTipoPersona; }
    public void setIdTipoPersona(int idTipoPersona) { this.idTipoPersona = idTipoPersona; }
    public boolean isActivarAcceso() { return activarAcceso; }
    public void setActivarAcceso(boolean activarAcceso) { this.activarAcceso = activarAcceso; }
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
}