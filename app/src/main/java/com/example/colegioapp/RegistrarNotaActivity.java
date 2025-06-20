package com.example.colegioapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarNotaActivity extends AppCompatActivity {

    private Spinner spinnerAnio, spinnerPeriodo, spinnerAsignatura, spinnerAlumno, spinnerRubrica;
    private EditText etNota;
    private Button btnRegistrarNota;
    private ProgressBar progressBar;
    private ApiService apiService;
    private int idUsuarioDocente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_nota);

        // Inicializar vistas
        spinnerAnio = findViewById(R.id.spinner_anio);
        spinnerPeriodo = findViewById(R.id.spinner_periodo);
        spinnerAsignatura = findViewById(R.id.spinner_asignatura);
        spinnerAlumno = findViewById(R.id.spinner_alumno);
        spinnerRubrica = findViewById(R.id.spinner_rubrica);
        etNota = findViewById(R.id.et_nota);
        btnRegistrarNota = findViewById(R.id.btn_registrar_nota);
        progressBar = findViewById(R.id.progress_bar);

        // Obtener idUsuarioDocente desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("colegioAppPrefs", MODE_PRIVATE);
        idUsuarioDocente = prefs.getInt("ID_USUARIO", 0);
        if (idUsuarioDocente == 0) {
            Toast.makeText(this, "No se encontró el usuario docente. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializar Retrofit
        apiService = RetrofitClient.getApiService(this);

        // Configurar validación dinámica
        setupValidation();

        // Cargar datos iniciales
        cargarAnios();
        cargarAsignaturas();
        cargarRubricas();

        // Configurar botón
        btnRegistrarNota.setOnClickListener(v -> registrarNota());
    }

    private void setupValidation() {
        // Validar campos para habilitar/deshabilitar el botón
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
            }
        };
        etNota.addTextChangedListener(textWatcher);

        // Validar spinners cuando cambian
        android.widget.AdapterView.OnItemSelectedListener spinnerListener = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                validateForm();
                if (parent == spinnerAnio) {
                    AnioEscolar anio = (AnioEscolar) parent.getItemAtPosition(position);
                    cargarPeriodos(anio.getID_ANIO_ESCOLAR());
                } else if (parent == spinnerAsignatura) {
                    Asignatura asignatura = (Asignatura) parent.getItemAtPosition(position);
                    cargarAlumnos(asignatura.getID_ASIGNATURA());
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                validateForm();
                if (parent == spinnerAnio) {
                    spinnerPeriodo.setAdapter(null);
                } else if (parent == spinnerAsignatura) {
                    spinnerAlumno.setAdapter(null);
                }
            }
        };
        spinnerAnio.setOnItemSelectedListener(spinnerListener);
        spinnerPeriodo.setOnItemSelectedListener(spinnerListener);
        spinnerAsignatura.setOnItemSelectedListener(spinnerListener);
        spinnerAlumno.setOnItemSelectedListener(spinnerListener);
        spinnerRubrica.setOnItemSelectedListener(spinnerListener);
    }

    private void validateForm() {
        boolean isValid = spinnerAnio.getSelectedItem() != null &&
                spinnerPeriodo.getSelectedItem() != null &&
                spinnerAsignatura.getSelectedItem() != null &&
                spinnerAlumno.getSelectedItem() != null &&
                spinnerRubrica.getSelectedItem() != null &&
                !etNota.getText().toString().trim().isEmpty();
        btnRegistrarNota.setEnabled(isValid);
    }

    private void cargarAnios() {
        showProgress(true);
        apiService.listarAnios().enqueue(new Callback<List<AnioEscolar>>() {
            @Override
            public void onResponse(Call<List<AnioEscolar>> call, Response<List<AnioEscolar>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<AnioEscolar> anios = response.body();
                    ArrayAdapter<AnioEscolar> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                            android.R.layout.simple_spinner_item, anios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAnio.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistrarNotaActivity.this, "No se encontraron años escolares", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AnioEscolar>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar años: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarPeriodos(int idAnioEscolar) {
        showProgress(true);
        spinnerPeriodo.setAdapter(null); // Limpiar spinner dependiente
        apiService.listarPeriodos(idAnioEscolar).enqueue(new Callback<List<Periodo>>() {
            @Override
            public void onResponse(Call<List<Periodo>> call, Response<List<Periodo>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Periodo> periodos = response.body();
                    ArrayAdapter<Periodo> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                            android.R.layout.simple_spinner_item, periodos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPeriodo.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistrarNotaActivity.this, "No se encontraron períodos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Periodo>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar períodos: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarAsignaturas() {
        showProgress(true);
        apiService.listarAsignaturas(idUsuarioDocente).enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(Call<List<Asignatura>> call, Response<List<Asignatura>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Asignatura> asignaturas = response.body();
                    ArrayAdapter<Asignatura> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                            android.R.layout.simple_spinner_item, asignaturas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAsignatura.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistrarNotaActivity.this, "No se encontraron asignaturas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Asignatura>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar asignaturas: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarAlumnos(int idAsignatura) {
        showProgress(true);
        spinnerAlumno.setAdapter(null); // Limpiar spinner dependiente
        apiService.listarAlumnos(idUsuarioDocente, idAsignatura).enqueue(new Callback<List<Alumno>>() {
            @Override
            public void onResponse(Call<List<Alumno>> call, Response<List<Alumno>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Alumno> alumnos = response.body();
                    ArrayAdapter<Alumno> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                            android.R.layout.simple_spinner_item, alumnos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAlumno.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistrarNotaActivity.this, "No se encontraron alumnos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Alumno>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar alumnos: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarRubricas() {
        showProgress(true);
        apiService.listarRubricas().enqueue(new Callback<List<Rubrica>>() {
            @Override
            public void onResponse(Call<List<Rubrica>> call, Response<List<Rubrica>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Rubrica> rubricas = response.body();
                    ArrayAdapter<Rubrica> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                            android.R.layout.simple_spinner_item, rubricas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRubrica.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistrarNotaActivity.this, "No se encontraron rúbricas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Rubrica>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar rúbricas: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registrarNota() {
        String notaText = etNota.getText().toString().trim();
        if (spinnerAnio.getSelectedItem() == null || spinnerPeriodo.getSelectedItem() == null ||
                spinnerAsignatura.getSelectedItem() == null || spinnerAlumno.getSelectedItem() == null ||
                spinnerRubrica.getSelectedItem() == null || notaText.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        float nota;
        try {
            nota = Float.parseFloat(notaText);
            if (nota < 0 || nota > 20) {
                Toast.makeText(this, "La nota debe estar entre 0 y 20", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una nota válida (número decimal)", Toast.LENGTH_SHORT).show();
            return;
        }

        AnioEscolar anio = (AnioEscolar) spinnerAnio.getSelectedItem();
        Periodo periodo = (Periodo) spinnerPeriodo.getSelectedItem();
        Asignatura asignatura = (Asignatura) spinnerAsignatura.getSelectedItem();
        Alumno alumno = (Alumno) spinnerAlumno.getSelectedItem();
        Rubrica rubrica = (Rubrica) spinnerRubrica.getSelectedItem();

        NotaRequest notaRequest = new NotaRequest(
                idUsuarioDocente,
                alumno.getID_USUARIO(),
                asignatura.getID_ASIGNATURA(),
                periodo.getID_PERIODO(),
                rubrica.getID_RUBRICA(),
                nota
        );

        showProgress(true);
        apiService.registrarNota(notaRequest).enqueue(new Callback<NotaResponse>() {
            @Override
            public void onResponse(Call<NotaResponse> call, Response<NotaResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistrarNotaActivity.this, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                    etNota.setText(""); // Limpiar campo de nota
                    validateForm(); // Actualizar estado del botón
                } else {
                    String errorMsg = "Error al registrar nota";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(RegistrarNotaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NotaResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al registrar nota: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegistrarNota.setEnabled(!show && btnRegistrarNota.isEnabled());
    }
}