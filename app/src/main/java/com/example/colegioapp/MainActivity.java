package com.example.colegioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.colegioapp.LoginRequest;
import com.example.colegioapp.LoginResponse;
import com.example.colegioapp.ApiService;
import com.example.colegioapp.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etUsuario, etContrasena;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);

        // Listener de botón
        btnLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(MainActivity.this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamada al login
            login(usuario, contrasena);
        });
    }

    private void login(String usuario, String contrasena) {
        ApiService apiService = RetrofitClient.getApiService();
        LoginRequest request = new LoginRequest(usuario, contrasena);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    // Guardar token localmente
                    SharedPreferences prefs = getSharedPreferences("colegioAppPrefs", MODE_PRIVATE);
                    prefs.edit().putString("TOKEN", token).apply();

                    Toast.makeText(MainActivity.this, "¡Login exitoso!", Toast.LENGTH_SHORT).show();

                    // Ir a HomeActivity
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
