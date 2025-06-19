package com.example.colegioapp;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private TextInputEditText etNombres, etApellidos, etDocumento, etFechaNacimiento, etCorreo, etTelefono,
            etUsuario, etContrasena, etRol;
    private TextInputLayout tilUsuario, tilContrasena, tilRol;
    private CheckBox cbActivarAcceso;
    private Button btnRegistrar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        // Inicializar ApiService desde RetrofitClient
        apiService = RetrofitClient.getApiService();

        // Inicializar vistas
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etDocumento = findViewById(R.id.etDocumento);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        etRol = findViewById(R.id.etRol);
        tilUsuario = findViewById(R.id.tilUsuario);
        tilContrasena = findViewById(R.id.tilContrasena);
        tilRol = findViewById(R.id.tilRol);
        cbActivarAcceso = findViewById(R.id.cbActivarAcceso);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Mostrar/ocultar campos de acceso según el checkbox
        cbActivarAcceso.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            tilUsuario.setVisibility(visibility);
            tilContrasena.setVisibility(visibility);
            tilRol.setVisibility(visibility);
        });

        // Acción del botón Registrar
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Limpiar errores previos
        clearErrors();

        // Obtener valores
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String documento = etDocumento.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String rol = etRol.getText().toString().trim();
        boolean activarAcceso = cbActivarAcceso.isChecked();

        // Validaciones
        boolean isValid = true;

        if (nombres.isEmpty()) {
            etNombres.setError("El nombre es requerido");
            isValid = false;
        }
        if (apellidos.isEmpty()) {
            etApellidos.setError("El apellido es requerido");
            isValid = false;
        }
        if (documento.isEmpty()) {
            etDocumento.setError("El documento es requerido");
            isValid = false;
        } else if (!documento.matches("\\d{8}")) {
            etDocumento.setError("El documento debe tener 8 dígitos");
            isValid = false;
        }
        if (fechaNacimiento.isEmpty()) {
            etFechaNacimiento.setError("La fecha es requerida");
            isValid = false;
        } else if (!isValidDate(fechaNacimiento)) {
            etFechaNacimiento.setError("Formato inválido (YYYY-MM-DD)");
            isValid = false;
        }
        if (correo.isEmpty()) {
            etCorreo.setError("El correo es requerido");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo inválido");
            isValid = false;
        }
        if (telefono.isEmpty()) {
            etTelefono.setError("El teléfono es requerido");
            isValid = false;
        } else if (!telefono.matches("\\d{9}")) {
            etTelefono.setError("El teléfono debe tener 9 dígitos");
            isValid = false;
        }

        if (activarAcceso) {
            if (usuario.isEmpty()) {
                etUsuario.setError("El usuario es requerido");
                isValid = false;
            }
            if (contrasena.isEmpty()) {
                etContrasena.setError("La contraseña es requerida");
                isValid = false;
            } else if (contrasena.length() < 6) {
                etContrasena.setError("La contraseña debe tener al menos 6 caracteres");
                isValid = false;
            }
            if (rol.isEmpty()) {
                etRol.setError("El ID de rol es requerido");
                isValid = false;
            } else {
                try {
                    Integer.parseInt(rol);
                } catch (NumberFormatException e) {
                    etRol.setError("ID de rol debe ser un número");
                    isValid = false;
                }
            }
        }

        if (!isValid) return;

        // Crear objeto de solicitud
        RegistroRequest request = new RegistroRequest(
                nombres, apellidos, documento, fechaNacimiento, correo, telefono,
                activarAcceso, activarAcceso ? usuario : null,
                activarAcceso ? contrasena : null, activarAcceso ? Integer.parseInt(rol) : 0
        );

        // Enviar solicitud POST
        Call<RegistroResponse> call = apiService.registrarUsuario(request);
        call.enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistroUsuarioActivity.this, response.body().getMensaje(), Toast.LENGTH_LONG).show();
                    finish(); // Volver a HomeActivity
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private void clearErrors() {
        etNombres.setError(null);
        etApellidos.setError(null);
        etDocumento.setError(null);
        etFechaNacimiento.setError(null);
        etCorreo.setError(null);
        etTelefono.setError(null);
        etUsuario.setError(null);
        etContrasena.setError(null);
        etRol.setError(null);
    }
}