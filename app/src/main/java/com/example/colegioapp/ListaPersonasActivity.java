package com.example.colegioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaPersonasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PersonaAdapter personaAdapter;
    private List<Persona> personaList;
    private List<Persona> filteredPersonaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personas);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar); // Corregido
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lista de Personas");

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPersonas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personaList = new ArrayList<>();
        filteredPersonaList = new ArrayList<>();
        personaAdapter = new PersonaAdapter(filteredPersonaList, persona -> {
            Intent intent = new Intent(ListaPersonasActivity.this, DetallesPersonaActivity.class);
            intent.putExtra("PERSONA", persona);
            startActivity(intent);
        });
        recyclerView.setAdapter(personaAdapter);

        // Llamar al endpoint
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.listarPersonas().enqueue(new Callback<List<Persona>>() {
            @Override
            public void onResponse(Call<List<Persona>> call, Response<List<Persona>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    personaList.clear();
                    personaList.addAll(response.body());
                    filteredPersonaList.clear();
                    filteredPersonaList.addAll(personaList);
                    personaAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListaPersonasActivity.this, "Error al cargar la lista: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Persona>> call, Throwable t) {
                Toast.makeText(ListaPersonasActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_personas, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar por nombre o apellido");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        return true;
    }

    private void filterList(String query) {
        filteredPersonaList.clear();
        if (query.isEmpty()) {
            filteredPersonaList.addAll(personaList);
        } else {
            query = query.toLowerCase();
            for (Persona persona : personaList) {
                String nombreCompleto = (persona.getNombres() + " " + persona.getApellidos()).toLowerCase();
                if (nombreCompleto.contains(query)) {
                    filteredPersonaList.add(persona);
                }
            }
        }
        personaAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}