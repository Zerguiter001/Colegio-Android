package com.example.colegioapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetallesPersonaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_persona);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalles de Persona");

        // Obtener la persona del Intent
        Persona persona = (Persona) getIntent().getSerializableExtra("PERSONA");

        // Configurar los TextViews
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvDocumento = findViewById(R.id.tvDocumento);
        TextView tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento);
        TextView tvCorreo = findViewById(R.id.tvCorreo);
        TextView tvTelefono = findViewById(R.id.tvTelefono);
        TextView tvTipoPersona = findViewById(R.id.tvTipoPersona);
        TextView tvTieneAcceso = findViewById(R.id.tvTieneAcceso);
        TextView tvUsuario = findViewById(R.id.tvUsuario);
        TextView tvRol = findViewById(R.id.tvRol);

        if (persona != null) {
            tvNombre.setText(persona.getNombres() + " " + persona.getApellidos());
            tvDocumento.setText(persona.getDocumentoIdentidad());
            // Formatear la fecha
            String fecha = persona.getFechaNacimiento();
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = isoFormat.parse(fecha);
                tvFechaNacimiento.setText(displayFormat.format(date));
            } catch (ParseException e) {
                tvFechaNacimiento.setText(fecha); // Fallback si el parseo falla
            }
            tvCorreo.setText(persona.getCorreo());
            tvTelefono.setText(persona.getTelefono());
            tvTipoPersona.setText(persona.getTipoPersona());
            tvTieneAcceso.setText(persona.getTieneAcceso());
            tvUsuario.setText(persona.getUsuario() != null ? persona.getUsuario() : "-");
            tvRol.setText(persona.getNombreRol() != null ? persona.getNombreRol() : "-");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}