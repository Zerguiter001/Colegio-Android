package com.example.colegioapp;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarNotaActivity extends AppCompatActivity {

    private Spinner spinnerAnio, spinnerPeriodo, spinnerAsignatura, spinnerAlumno, spinnerRubrica;
    private EditText etNota;
    private TextView tvNotaExistente;
    private Button btnRegistrarNota;
    private ProgressBar progressBar;
    private ApiService apiService;
    private int idUsuarioDocente;
    private static final String TAG = "RegistrarNota";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_nota);
        Log.d(TAG, "onCreate iniciado");

        try {
            // Inicializar vistas
            spinnerAnio = findViewById(R.id.spinner_anio);
            spinnerPeriodo = findViewById(R.id.spinner_periodo);
            spinnerAsignatura = findViewById(R.id.spinner_asignatura);
            spinnerAlumno = findViewById(R.id.spinner_alumno);
            spinnerRubrica = findViewById(R.id.spinner_rubrica);
            etNota = findViewById(R.id.et_nota);
            tvNotaExistente = findViewById(R.id.tv_nota_existente);
            btnRegistrarNota = findViewById(R.id.btn_registrar_nota);
            progressBar = findViewById(R.id.progress_bar);
            Log.d(TAG, "Vistas inicializadas");

            // Obtener ID del usuario desde SharedPreferences
            SharedPreferences prefs = getSharedPreferences("colegioAppPrefs", MODE_PRIVATE);
            idUsuarioDocente = prefs.getInt("ID_USUARIO", -1);
            if (idUsuarioDocente == -1) {
                Log.e(TAG, "No se encontró ID_USUARIO en SharedPreferences");
                Toast.makeText(this, "Error: No se encontró el ID del usuario", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Log.d(TAG, "idUsuarioDocente: " + idUsuarioDocente);

            // Inicializar Retrofit
            apiService = RetrofitClient.getApiService(this);
            Log.d(TAG, "Retrofit inicializado");

            // Configurar validación dinámica
            setupValidation();
            Log.d(TAG, "Validación configurada");

            // Cargar datos iniciales
            cargarAnios();
            cargarAsignaturas();
            cargarRubricas();
            Log.d(TAG, "Carga de datos iniciada");

            // Configurar botón
            btnRegistrarNota.setOnClickListener(v -> confirmarRegistroNota());

        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error al iniciar actividad: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupValidation() {
        Log.d(TAG, "Configurando validación");
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
                        } else if (parent == spinnerAlumno || parent == spinnerRubrica || parent == spinnerPeriodo) {
                            verificarNotaExistente();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en spinnerListener: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error en selección: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    validateForm();
                    tvNotaExistente.setVisibility(View.GONE);
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
            Log.e(TAG, "Error en setupValidation: " + e.getMessage(), e);
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
            Log.d(TAG, "Formulario válido: " + isValid);
        } catch (Exception e) {
            Log.e(TAG, "Error en validateForm: " + e.getMessage(), e);
        }
    }

    private void verificarNotaExistente() {
        try {
            if (spinnerAlumno.getSelectedItem() == null || spinnerAsignatura.getSelectedItem() == null ||
                    spinnerPeriodo.getSelectedItem() == null || spinnerRubrica.getSelectedItem() == null) {
                tvNotaExistente.setVisibility(View.GONE);
                return;
            }

            Alumno alumno = (Alumno) spinnerAlumno.getSelectedItem();
            Asignatura asignatura = (Asignatura) spinnerAsignatura.getSelectedItem();
            Periodo periodo = (Periodo) spinnerPeriodo.getSelectedItem();
            Rubrica rubrica = (Rubrica) spinnerRubrica.getSelectedItem();

            showProgress(true);
            apiService.verificarNotaExistente(idUsuarioDocente, asignatura.getID_ASIGNATURA(),
                    periodo.getID_PERIODO(), alumno.getID_USUARIO()).enqueue(new Callback<List<NotaExistenteResponse>>() {
                @Override
                public void onResponse(Call<List<NotaExistenteResponse>> call, Response<List<NotaExistenteResponse>> response) {
                    showProgress(false);
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            for (NotaExistenteResponse nota : response.body()) {
                                if (nota.getRUBRICA().equals(rubrica.toString()) && nota.isNotaRegistrada()) {
                                    tvNotaExistente.setText("Nota existente: " + nota.getNotaValue() + " (" + nota.getDESCRIPCION_RUBRICA() + ")");
                                    tvNotaExistente.setVisibility(View.VISIBLE);
                                    etNota.setText(String.valueOf(nota.getNotaValue()));
                                    return;
                                }
                            }
                            tvNotaExistente.setVisibility(View.GONE);
                            etNota.setText("");
                        } else {
                            tvNotaExistente.setVisibility(View.GONE);
                            etNota.setText("");
                            Log.d(TAG, "No se encontró nota existente");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en verificarNotaExistente onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al verificar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<NotaExistenteResponse>> call, Throwable t) {
                    showProgress(false);
                    tvNotaExistente.setVisibility(View.GONE);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al verificar nota: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error verificarNotaExistente: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error en verificarNotaExistente: " + e.getMessage(), e);
            Toast.makeText(this, "Error al verificar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarRegistroNota() {
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

            Alumno alumno = (Alumno) spinnerAlumno.getSelectedItem();
            Asignatura asignatura = (Asignatura) spinnerAsignatura.getSelectedItem();
            Periodo periodo = (Periodo) spinnerPeriodo.getSelectedItem();
            Rubrica rubrica = (Rubrica) spinnerRubrica.getSelectedItem();
            AnioEscolar anio = (AnioEscolar) spinnerAnio.getSelectedItem();

            String mensajeConfirmacion = String.format(
                    "Registrar nota %.1f para %s\nAsignatura: %s\nPeríodo: %s\nRúbrica: %s\nAño: %d\n¿Confirmar?",
                    nota, alumno.toString(), asignatura.toString(), periodo.toString(), rubrica.toString(), anio.getANIO());

            new AlertDialog.Builder(this)
                    .setTitle("Confirmar Registro de Nota")
                    .setMessage(mensajeConfirmacion)
                    .setPositiveButton("Aceptar", (dialog, which) -> registrarNota())
                    .setNegativeButton("Cancelar", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error en confirmarRegistroNota: " + e.getMessage(), e);
            Toast.makeText(this, "Error al confirmar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarNota() {
        try {
            String notaText = etNota.getText().toString().trim();
            float nota = Float.parseFloat(notaText);

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
                            Toast.makeText(RegistrarNotaActivity.this,
                                    "Nota registrada con éxito para " + alumno.toString() + ": " + response.body().getMensaje(),
                                    Toast.LENGTH_LONG).show();
                            etNota.setText("");
                            tvNotaExistente.setVisibility(View.GONE);
                            validateForm();
                            verificarNotaExistente(); // Actualizar estado de nota existente
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
                        Log.e(TAG, "Error en registrarNota onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al registrar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<NotaResponse> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al registrar nota: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error registrarNota: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error en registrarNota: " + e.getMessage(), e);
            Toast.makeText(this, "Error al registrar nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Log.d(TAG, "Años cargados: " + anios.size());
                        } else {
                            Log.d(TAG, "No se encontraron años escolares");
                            new AlertDialog.Builder(RegistrarNotaActivity.this)
                                    .setTitle("Sin Años Escolares")
                                    .setMessage("No hay años escolares disponibles para registrar notas.")
                                    .setPositiveButton("Aceptar", (dialog, which) -> {
                                        finish(); // Regresar a HomeActivity
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en cargarAnios onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar años: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<AnioEscolar>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar años: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error cargarAnios: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error al iniciar cargarAnios: " + e.getMessage(), e);
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
                            Log.d(TAG, "Períodos cargados: " + periodos.size());
                        } else {
                            Log.d(TAG, "No se encontraron períodos");
                            new AlertDialog.Builder(RegistrarNotaActivity.this)
                                    .setTitle("Sin Períodos")
                                    .setMessage("No hay períodos disponibles para el año escolar seleccionado.")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en cargarPeriodos onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar períodos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Periodo>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar períodos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error cargarPeriodos: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error al iniciar cargarPeriodos: " + e.getMessage(), e);
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
                        if (response.isSuccessful() && response.body() != null) {
                            List<Asignatura> asignaturas = response.body();
                            if (!asignaturas.isEmpty()) {
                                ArrayAdapter<Asignatura> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                        android.R.layout.simple_spinner_item, asignaturas);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerAsignatura.setAdapter(adapter);
                                Log.d(TAG, "Asignaturas cargadas: " + asignaturas.size());
                            } else {
                                Log.d(TAG, "No se encontraron asignaturas");
                                new AlertDialog.Builder(RegistrarNotaActivity.this)
                                        .setTitle("Sin Asignaturas")
                                        .setMessage("No tienes asignaturas asignadas para registrar notas.")
                                        .setPositiveButton("Aceptar", (dialog, which) -> {
                                            finish(); // Regresar a HomeActivity
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        } else {
                            Log.w(TAG, "Respuesta no exitosa o cuerpo nulo al cargar asignaturas. Código HTTP: " + response.code());
                            if (response.errorBody() != null) {
                                Log.w(TAG, "Cuerpo del error: " + response.errorBody().string());
                            }
                            new AlertDialog.Builder(RegistrarNotaActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Error al cargar asignaturas. Por favor, intenta de nuevo.")
                                    .setPositiveButton("Aceptar", (dialog, which) -> {
                                        finish(); // Regresar a HomeActivity
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en cargarAsignaturas onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar asignaturas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Asignatura>> call, Throwable t) {
                    showProgress(false);
                    Log.e(TAG, "Error cargarAsignaturas: " + t.getMessage(), t);
                    new AlertDialog.Builder(RegistrarNotaActivity.this)
                            .setTitle("Error de Conexión")
                            .setMessage("No se pudo conectar al servidor para cargar asignaturas: " + t.getMessage())
                            .setPositiveButton("Aceptar", (dialog, which) -> {
                                finish(); // Regresar a HomeActivity
                            })
                            .setCancelable(false)
                            .show();
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error al iniciar cargarAsignaturas: " + e.getMessage(), e);
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
                        if (response.isSuccessful() && response.body() != null) {
                            List<Alumno> alumnos = response.body();
                            if (!alumnos.isEmpty()) {
                                ArrayAdapter<Alumno> adapter = new ArrayAdapter<>(RegistrarNotaActivity.this,
                                        android.R.layout.simple_spinner_item, alumnos);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerAlumno.setAdapter(adapter);
                                Log.d(TAG, "Alumnos cargados: " + alumnos.size());
                            } else {
                                Log.d(TAG, "No se encontraron alumnos");
                                new AlertDialog.Builder(RegistrarNotaActivity.this)
                                        .setTitle("Sin Alumnos")
                                        .setMessage("No hay alumnos inscritos en esta asignatura.")
                                        .setPositiveButton("Aceptar", null)
                                        .show();
                            }
                        } else {
                            Log.w(TAG, "Respuesta no exitosa o cuerpo nulo al cargar alumnos. Código HTTP: " + response.code());
                            if (response.errorBody() != null) {
                                Log.w(TAG, "Cuerpo del error: " + response.errorBody().string());
                            }
                            new AlertDialog.Builder(RegistrarNotaActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Error al cargar alumnos. Por favor, intenta de nuevo.")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en cargarAlumnos onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar alumnos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Alumno>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar alumnos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error cargarAlumnos: " + t.getMessage(), t);
                    new AlertDialog.Builder(RegistrarNotaActivity.this)
                            .setTitle("Error de Conexión")
                            .setMessage("No se pudo conectar al servidor para cargar alumnos: " + t.getMessage())
                            .setPositiveButton("Aceptar", null)
                            .show();
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error al iniciar cargarAlumnos: " + e.getMessage(), e);
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
                            Log.d(TAG, "Rúbricas cargadas: " + rubricas.size());
                        } else {
                            Log.d(TAG, "No se encontraron rúbricas");
                            new AlertDialog.Builder(RegistrarNotaActivity.this)
                                    .setTitle("Sin Rúbricas")
                                    .setMessage("No hay rúbricas disponibles para registrar notas.")
                                    .setPositiveButton("Aceptar", (dialog, which) -> {
                                        finish(); // Regresar a HomeActivity
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error en cargarRubricas onResponse: " + e.getMessage(), e);
                        Toast.makeText(RegistrarNotaActivity.this, "Error al cargar rúbricas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Rubrica>> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegistrarNotaActivity.this, "Error de conexión al cargar rúbricas: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error cargarRubricas: " + t.getMessage(), t);
                    new AlertDialog.Builder(RegistrarNotaActivity.this)
                            .setTitle("Error de Conexión")
                            .setMessage("No se pudo conectar al servidor para cargar rúbricas: " + t.getMessage())
                            .setPositiveButton("Aceptar", (dialog, which) -> {
                                finish(); // Regresar a HomeActivity
                            })
                            .setCancelable(false)
                            .show();
                }
            });
        } catch (Exception e) {
            showProgress(false);
            Log.e(TAG, "Error al iniciar cargarRubricas: " + e.getMessage(), e);
            Toast.makeText(this, "Error al cargar rúbricas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(boolean show) {
        try {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            btnRegistrarNota.setEnabled(!show && btnRegistrarNota.isEnabled());
            Log.d(TAG, "ProgressBar actualizado: " + show);
        } catch (Exception e) {
            Log.e(TAG, "Error en showProgress: " + e.getMessage(), e);
        }
    }
}