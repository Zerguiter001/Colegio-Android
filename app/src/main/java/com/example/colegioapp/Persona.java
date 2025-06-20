package com.example.colegioapp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Persona implements Serializable {
    @SerializedName("ID_PERSONA")
    private int idPersona;

    @SerializedName("NOMBRES")
    private String nombres;

    @SerializedName("APELLIDOS")
    private String apellidos;

    @SerializedName("DOCUMENTO_IDENTIDAD")
    private String documentoIdentidad;

    @SerializedName("FECHA_NACIMIENTO")
    private String fechaNacimiento;

    @SerializedName("CORREO")
    private String correo;

    @SerializedName("TELEFONO")
    private String telefono;

    @SerializedName("ESTADO_PERSONA")
    private boolean estadoPersona;

    @SerializedName("TIPO_PERSONA")
    private String tipoPersona;

    @SerializedName("TIENE_ACCESO")
    private String tieneAcceso;

    @SerializedName("USUARIO")
    private String usuario;

    @SerializedName("NOMBRE_ROL")
    private String nombreRol;

    @SerializedName("ESTADO_USUARIO")
    private Boolean estadoUsuario;

    // Getters
    public int getIdPersona() {
        return idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isEstadoPersona() {
        return estadoPersona;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public String getTieneAcceso() {
        return tieneAcceso;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public Boolean getEstadoUsuario() {
        return estadoUsuario;
    }
}