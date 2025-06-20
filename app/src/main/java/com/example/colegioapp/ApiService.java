package com.example.colegioapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("usuarios/registrar")
    Call<RegistroResponse> registrarUsuario(@Body RegistroRequest registroRequest);

    @GET("usuarios/listar")
    Call<List<Persona>> listarPersonas();
}