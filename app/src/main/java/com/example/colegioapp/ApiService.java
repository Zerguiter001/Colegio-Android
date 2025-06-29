package com.example.colegioapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("menu/gestionarMenu")
    Call<MenuResponse> gestionarMenu(@Body MenuRequest request);


    @POST("usuarios/registrar")
    Call<RegistroResponse> registrarUsuario(@Body RegistroRequest registroRequest);

    @GET("usuarios/listar")
    Call<List<Persona>> listarPersonas();

    @GET("notas/anios")
    Call<List<AnioEscolar>> listarAnios();

    @GET("notas/periodos/{idAnioEscolar}")
    Call<List<Periodo>> listarPeriodos(@Path("idAnioEscolar") int idAnioEscolar);

    @GET("notas/asignaturas/{idUsuarioDocente}")
    Call<List<Asignatura>> listarAsignaturas(@Path("idUsuarioDocente") int idUsuarioDocente);

    @GET("notas/alumnos/{idUsuarioDocente}/{idAsignatura}")
    Call<List<Alumno>> listarAlumnos(@Path("idUsuarioDocente") int idUsuarioDocente, @Path("idAsignatura") int idAsignatura);

    @GET("notas/rubricas")
    Call<List<Rubrica>> listarRubricas();

    @POST("notas/registrar")
    Call<NotaResponse> registrarNota(@Body NotaRequest notaRequest);

    @GET("notas/alumno/{idUsuarioDocente}/{idAsignatura}/{idPeriodo}/{idUsuarioAlumno}")
    Call<List<NotaExistenteResponse>> verificarNotaExistente(
            @Path("idUsuarioDocente") int idUsuarioDocente,
            @Path("idAsignatura") int idAsignatura,
            @Path("idPeriodo") int idPeriodo,
            @Path("idUsuarioAlumno") int idUsuarioAlumno
    );

}