package com.example.colegioapp;

public class MenuRequest {
    private String accion;
    private Integer idRol;
    private Integer idMenu;

    public MenuRequest(String accion, Integer idRol, Integer idMenu) {
        this.accion = accion;
        this.idRol = idRol;
        this.idMenu = idMenu;
    }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }
    public Integer getIdMenu() { return idMenu; }
    public void setIdMenu(Integer idMenu) { this.idMenu = idMenu; }
}