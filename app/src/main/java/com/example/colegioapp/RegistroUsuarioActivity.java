package com.example.colegioapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private TextInputEditText etNombres, etApellidos, etDocumento, etFechaNacimiento, etCorreo, etTelefono;
    private TextInputLayout tilUsuario, tilContrasena;
    private CheckBox cbActivarAcceso;
    private Spinner spinnerTipoPersona, spinnerRol;
    private Button btnRegistrar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        // Inicializar ApiService desde RetrofitClient
        apiService = RetrofitClient.getApiService(this); // Corregido: pasar el contexto

        // Inicializar vistas
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etDocumento = findViewById(R.id.etDocumento);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        cbActivarAcceso = findViewById(R.id.cbActivarAcceso);
        spinnerTipoPersona = findViewById(R.id.spinnerTipoPersona);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Configurar Spinners
        ArrayAdapter<CharSequence> tipoPersonaAdapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_persona, android.R.layout.simple_spinner_item);
        tipoPersonaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPersona.setAdapter(tipoPersonaAdapter);

        ArrayAdapter<CharSequence> rolAdapter = ArrayAdapter.createFromResource(this,
                R.array.roles, android.R.layout.simple_spinner_item);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);

        // Configurar DatePicker
        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        // Mostrar/ocultar spinner de rol según el checkbox
        cbActivarAcceso.setOnCheckedChangeListener((buttonView, isChecked) -> {
            spinnerRol.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Acción del botón Registrar
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    etFechaNacimiento.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void registrarUsuario() {
        // Limpiar errores previos
        clearErrors();

        // Obtener valores
        String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String documento = etDocumento.getText() != null ? etDocumento.getText().toString().trim() : "";
        String fechaNacimiento = etFechaNacimiento.getText() != null ? etFechaNacimiento.getText().toString().trim() : "";
        String correo = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        boolean activarAcceso = cbActivarAcceso.isChecked();
        int idTipoPersona = getResources().getIntArray(R.array.tipos_persona_ids)[spinnerTipoPersona.getSelectedItemPosition()];
        int idRol = activarAcceso ? getResources().getIntArray(R.array.roles_ids)[spinnerRol.getSelectedItemPosition()] : 0;

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
            etFechaNacimiento.setError("Formato inválido (DD/MM/YYYY)");
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
        if (idTipoPersona == 0) {
            Toast.makeText(this, "Seleccione un tipo de persona", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (activarAcceso && idRol == 0) {
            Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) return;

        // Convertir fecha de DD/MM/YYYY a YYYY-MM-DD para el backend
        String fechaNacimientoBackend;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(fechaNacimiento);
            fechaNacimientoBackend = outputFormat.format(date);
        } catch (ParseException e) {
            Toast.makeText(this, "Error al convertir la fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de solicitud
        RegistroRequest request = new RegistroRequest(
                nombres, apellidos, documento, fechaNacimientoBackend, correo, telefono,
                idTipoPersona, activarAcceso, idRol
        );

        // Enviar solicitud POST
        Call<RegistroResponse> call = apiService.registrarUsuario(request);
        call.enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegistroResponse registroResponse = response.body();
                    String mensaje = registroResponse.getMensaje();
                    if (activarAcceso && registroResponse.getUsuario() != null && registroResponse.getContrasena() != null) {
                        mensaje += String.format("\nUsuario: %s\nContraseña: %s", registroResponse.getUsuario(), registroResponse.getContrasena());
                    }
                    Toast.makeText(RegistroUsuarioActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    finish(); // Volver a HomeActivity
                } else {
                    String errorMessage = response.message();
                    try {
                        // Intentar parsear el mensaje de error del backend
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                            if (errorMessage.contains("El documento de identidad ya está registrado")) {
                                etDocumento.setError("El DNI ya está registrado");
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // Manejo de excepción en caso de que no se pueda leer el errorBody
                    }
                    Toast.makeText(RegistroUsuarioActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private void clearErrors() {
        if (etNombres != null) etNombres.setError(null);
        if (etApellidos != null) etApellidos.setError(null);
        if (etDocumento != null) etDocumento.setError(null);
        if (etFechaNacimiento != null) etFechaNacimiento.setError(null);
        if (etCorreo != null) etCorreo.setError(null);
        if (etTelefono != null) etTelefono.setError(null);
    }
}