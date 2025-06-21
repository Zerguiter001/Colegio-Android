package com.example.colegioapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
        Log.d("RegistrarNota", "onCreate iniciado");

        try {
            // Inicializar vistas
            spinnerAnio = findViewById(R.id.spinner_anio);
            spinnerPeriodo = findViewById(R.id.spinner_periodo);
            spinnerAsignatura = findViewById(R.id.spinner_asignatura);
            spinnerAlumno = findViewById(R.id.spinner_alumno);
            spinnerRubrica = findViewById(R.id.spinner_rubrica);
            etNota = findViewById(R.id.et_nota);
            btnRegistrarNota = findViewById(R.id.btn_registrar_nota);
            progressBar = findViewById(R.id.progress_bar);
            Log.d("RegistrarNota", "Vistas inicializadas");

            // Obtener ID del usuario desde SharedPreferences
            SharedPreferences prefs = getSharedPreferences("colegioAppPrefs", MODE_PRIVATE);
            idUsuarioDocente = prefs.getInt("ID_USUARIO", -1);
            if (idUsuarioDocente == -1) {
                Log.e("RegistrarNota", "No se encontró ID_USUARIO en SharedPreferences");
                Toast.makeText(this, "Error: No se encontró el ID del usuario", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Log.d("RegistrarNota", "idUsuarioDocente: " + idUsuarioDocente);

            // Inicializar Retrofit
            apiService = RetrofitClient.getApiService(this);
            Log.d("RegistrarNota", "Retrofit inicializado");

            // Configurar validación dinámica
            setupValidation();
            Log.d("RegistrarNota", "Validación configurada");

            // Cargar datos iniciales
            cargarAnios();
            cargarAsignaturas();
            cargarRubricas();
            Log.d("RegistrarNota", "Carga de datos iniciada");

            // Configurar botón
            btnRegistrarNota.setOnClickListener(v -> registrarNota());

        } catch (Exception e) {
            Log.e("RegistrarNota", "Error en onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error al iniciar actividad: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupValidation() {
        Log.d("RegistrarNota", "Configurando validación");
        try {
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

            android.widget.AdapterView.OnItemSelectedListener spinnerListener = new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    validateForm();
                    try {
                        if (parent == spinnerAnio && parent.getItemAtPosition(position) != null) {
                            AnioEscolar anio = (AnioEscolar) parent.getItemAtPosition(position);
                            cargarPeriodos(anio.getID_ANIO_ESCOLAR());
                        } else if (parent == spinnerAsignatura && parent.getItemAtPosition(position) != null) {
                            Asignatura asignatura = (Asignatura) parent.getItemAtPosition(position);
                            cargarAlumnos(asignatura.getID_ASIGNATURA());
                        }
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en spinnerListener: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error en selección: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Log.e("RegistrarNota", "Error en setupValidation: " + e.getMessage(), e);
            Toast.makeText(this, "Error en validación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validateForm() {
        try {
            boolean isValid = spinnerAnio.getSelectedItem() != null &&
                    spinnerPeriodo.getSelectedItem() != null &&
                    spinnerAsignatura.getSelectedItem() != null &&
                    spinnerAlumno.getSelectedItem() != null &&
                    spinnerRubrica.getSelectedItem() != null &&
                    !etNota.getText().toString().trim().isEmpty();
            btnRegistrarNota.setEnabled(isValid);
            Log.d("RegistrarNota", "Formulario válido: " + isValid);
        } catch (Exception e) {
            Log.e("RegistrarNota", "Error en validateForm: " + e.getMessage(), e);
        }
    }

    private void cargarAnios() {
        showProgress(true);
        try {
            apiService.listarAnios().enqueue(new Callback<List<AnioEscolar>>() {
                @Override
                public void onResponse(Call<List<AnioEscolar>> call, Response<List<AnioEscolar>> response) {
                    showProgress(false);
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<AnioEscolar> anios = response.body();
                            ArrayAdapter<AnioEscolar> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                    android.R.layout.simple_spinner_item, anios);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAnio.setAdapter(adapter);
                            Log.d("RegistrarNota", "Años cargados: " + anios.size());
                        } else {
                            Toast.makeText(RegistrarNotaActivity.this, "No se encontraron años escolares", Toast.LENGTH_SHORT).show();
                            Log.d("RegistrarNota", "No se encontraron años escolares");
                        }
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en cargarAnios onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar años: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<AnioEscolar>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar años: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RegistrarNota", "Error cargarAnios: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e("RegistrarNota", "Error al iniciar cargarAnios: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar años: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPeriodos(int idAnioEscolar) {
        showProgress(true);
        try {
            spinnerPeriodo.setAdapter(null);
            apiService.listarPeriodos(idAnioEscolar).enqueue(new Callback<List<Periodo>>() {
                @Override
                public void onResponse(Call<List<Periodo>> call, Response<List<Periodo>> response) {
                    showProgress(false);
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<Periodo> periodos = response.body();
                            ArrayAdapter<Periodo> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                    android.R.layout.simple_spinner_item, periodos);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPeriodo.setAdapter(adapter);
                            Log.d("RegistrarNota", "Períodos cargados: " + periodos.size());
                        } else {
                            Toast.makeText(RegistrarNotaActivity.this, "No se encontraron períodos", Toast.LENGTH_SHORT).show();
                            Log.d("RegistrarNota", "No se encontraron períodos");
                        }
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en cargarPeriodos onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar períodos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Periodo>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar períodos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RegistrarNota", "Error cargarPeriodos: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e("RegistrarNota", "Error al iniciar cargarPeriodos: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar períodos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarAsignaturas() {
        showProgress(true);
        try {
            apiService.listarAsignaturas(idUsuarioDocente).enqueue(new Callback<List<Asignatura>>() {
                @Override
                public void onResponse(Call<List<Asignatura>> call, Response<List<Asignatura>> response) {
                    showProgress(false);
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<Asignatura> asignaturas = response.body();
                            ArrayAdapter<Asignatura> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                    android.R.layout.simple_spinner_item, asignaturas);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAsignatura.setAdapter(adapter);
                            Log.d("RegistrarNota", "Asignaturas cargadas: " + asignaturas.size());
                        } else {
                            Toast.makeText(RegistrarNotaActivity.this, "No se encontraron asignaturas", Toast.LENGTH_SHORT).show();
                            Log.d("RegistrarNota", "No se encontraron asignaturas");
                        }
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en cargarAsignaturas onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar asignaturas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Asignatura>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar asignaturas: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RegistrarNota", "Error cargarAsignaturas: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e("RegistrarNota", "Error al iniciar cargarAsignaturas: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar asignaturas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarAlumnos(int idAsignatura) {
        showProgress(true);
        try {
            spinnerAlumno.setAdapter(null);
            apiService.listarAlumnos(idUsuarioDocente, idAsignatura).enqueue(new Callback<List<Alumno>>() {
                @Override
                public void onResponse(Call<List<Alumno>> call, Response<List<Alumno>> response) {
                    showProgress(false);
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<Alumno> alumnos = response.body();
                            ArrayAdapter<Alumno> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                    android.R.layout.simple_spinner_item, alumnos);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAlumno.setAdapter(adapter);
                            Log.d("RegistrarNota", "Alumnos cargados: " + alumnos.size());
                        } else {
                            Toast.makeText(RegistrarNotaActivity.this, "No se encontraron alumnos", Toast.LENGTH_SHORT).show();
                            Log.d("RegistrarNota", "No se encontraron alumnos");
                        }
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en cargarAlumnos onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar alumnos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Alumno>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar alumnos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RegistrarNota", "Error cargarAlumnos: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e("RegistrarNota", "Error al iniciar cargarAlumnos: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar alumnos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarRubricas() {
        showProgress(true);
        try {
            apiService.listarRubricas().enqueue(new Callback<List<Rubrica>>() {
                @Override
                public void onResponse(Call<List<Rubrica>> call, Response<List<Rubrica>> response) {
                    showProgress(false);
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<Rubrica> rubricas = response.body();
                            ArrayAdapter<Rubrica> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                    android.R.layout.simple_spinner_item, rubricas);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerRubrica.setAdapter(adapter);
                            Log.d("RegistrarNota", "Rúbricas cargadas: " + rubricas.size());
                        } else {
                            Toast.makeText(RegistrarNotaActivity.this, "No se encontraron rúbricas", Toast.LENGTH_SHORT).show();
                            Log.d("RegistrarNota", "No se encontraron rúbricas");
                        }
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en cargarRubricas onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar rúbricas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Rubrica>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar rúbricas: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RegistrarNota", "Error cargarRubricas: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e("RegistrarNota", "Error al iniciar cargarRubricas: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar rúbricas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarNota() {
        try {
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
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(RegistrarNotaActivity.this, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                            etNota.setText("");
                            validateForm();
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
                    } catch (Exception e) {
                        Log.e("RegistrarNota", "Error en registrarNota onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al registrar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<NotaResponse> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al registrar nota: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RegistrarNota", "Error registrarNota: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e("RegistrarNota", "Error en registrarNota: " + e.getMessage(), e);
            Toast.makeText(this, "Error al registrar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(boolean show) {
        try {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            btnRegistrarNota.setEnabled(!show && btnRegistrarNota.isEnabled());
            Log.d("RegistrarNota", "ProgressBar actualizado: " + show);
        } catch (Exception e) {
            Log.e("RegistrarNota", "Error en showProgress: " + e.getMessage(), e);
        }
    }
}