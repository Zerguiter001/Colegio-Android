package com.example.colegioapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MenuResponse {
    private String mensaje;
    private List<MenuItem> datos;

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public List<MenuItem> getDatos() { return datos; }
    public void setDatos(List<MenuItem> datos) { this.datos = datos; }

    public static class MenuItem {
        @SerializedName("ID_MENU")
        private int idMenu;

        @SerializedName("NOMBRE_MENU")
        private String nombreMenu;

        @SerializedName("DESCRIPCION")
        private String descripcion;

        @SerializedName("TIENE_ACCESO")
        private String tieneAcceso;

        @SerializedName("ESTADO_MENU")
        private boolean estadoMenu;

        public int getIdMenu() { return idMenu; }
        public void setIdMenu(int idMenu) { this.idMenu = idMenu; }

        public String getNombreMenu() { return nombreMenu; }
        public void setNombreMenu(String nombreMenu) { this.nombreMenu = nombreMenu; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getTieneAcceso() { return tieneAcceso; }
        public void setTieneAcceso(String tieneAcceso) { this.tieneAcceso = tieneAcceso; }

        public boolean isEstadoMenu() { return estadoMenu; }
        public void setEstadoMenu(boolean estadoMenu) { this.estadoMenu = estadoMenu; }
    }
}
