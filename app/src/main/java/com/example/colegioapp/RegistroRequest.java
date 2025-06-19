package com.example.colegioapp;

public class RegistroRequest {
    private String nombres;
    private String apellidos;
    private String documento;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private boolean activarAcceso;
    private String usuario;
    private String contrasena;
    private int idRol;

    public RegistroRequest(String nombres, String apellidos, String documento, String fechaNacimiento,
                           String correo, String telefono, boolean activarAcceso, String usuario,
                           String contrasena, int idRol) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.activarAcceso = activarAcceso;
        this.usuario = usuario;
        this.contrasena = contrasena;
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
    public boolean isActivarAcceso() { return activarAcceso; }
    public void setActivarAcceso(boolean activarAcceso) { this.activarAcceso = activarAcceso; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
}