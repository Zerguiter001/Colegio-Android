package com.example.colegioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            // Configurar Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Configurar DrawerLayout y NavigationView
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            // Configurar el ícono de hamburguesa para abrir/cerrar el sidebar
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            // Configurar el título
            TextView mensaje = findViewById(R.id.tvMensaje);
            mensaje.setText("Bienvenido al sistema escolar");

            // Configurar acciones del menú del sidebar
            navigationView.setNavigationItemSelectedListener(this);

            // Cargar el menú dinámicamente según el rol
            cargarMenuPorRol();
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error al iniciar HomeActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarMenuPorRol() {
        SharedPreferences prefs = getSharedPreferences("colegioAppPrefs", MODE_PRIVATE);
        int idRol = prefs.getInt("ID_ROL", 0);

        if (idRol == 0) {
            Log.e(TAG, "No se encontró ID_ROL en SharedPreferences");
            Toast.makeText(this, "Error: No se encontró el rol del usuario", Toast.LENGTH_LONG).show();
            // Añadir solo "Cerrar Sesión" para permitir salir
            Menu menu = navigationView.getMenu();
            menu.clear();
            menu.add(Menu.NONE, R.id.nav_cerrar_sesion, Menu.NONE, "Cerrar Sesión")
                    .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
            return;
        }

        Log.d(TAG, "Cargando menú para ID_ROL: " + idRol);

        ApiService apiService = RetrofitClient.getApiService(this);
        MenuRequest request = new MenuRequest("LISTAR", idRol, null);

        apiService.gestionarMenu(request).enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {
                try {
                    Log.d(TAG, "Respuesta HTTP: " + response.code());
                    Log.d(TAG, "Respuesta cruda: " + response.raw().toString());
                    if (response.isSuccessful() && response.body() != null) {
                        List<MenuResponse.MenuItem> menuItems = response.body().getDatos();
                        Log.d(TAG, "Elementos del menú recibidos: " + (menuItems != null ? menuItems.size() : "null"));

                        Menu menu = navigationView.getMenu();
                        menu.clear(); // Limpiar el menú existente

                        if (menuItems != null) {
                            for (MenuResponse.MenuItem item : menuItems) {
                                String tieneAcceso = item.getTieneAcceso();
                                String nombreMenu = item.getNombreMenu();
                                Log.d(TAG, "Procesando menú: " + nombreMenu + ", TieneAcceso: " + tieneAcceso);

                                if ("Sí".equalsIgnoreCase(tieneAcceso)) {
                                    int menuItemId = getMenuItemId(nombreMenu);
                                    if (menuItemId != -1) {
                                        menu.add(Menu.NONE, menuItemId, Menu.NONE, nombreMenu)
                                                .setIcon(getMenuItemIcon(nombreMenu));
                                        Log.d(TAG, "Añadido al menú: " + nombreMenu);
                                    } else {
                                        Log.w(TAG, "No se encontró ID para el menú: " + nombreMenu);
                                    }
                                }
                            }
                        } else {
                            Log.w(TAG, "Lista de menús es null");
                        }

                        // Siempre añadir "Cerrar Sesión"
                        menu.add(Menu.NONE, R.id.nav_cerrar_sesion, Menu.NONE, "Cerrar Sesión")
                                .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                        Log.d(TAG, "Añadido 'Cerrar Sesión' al menú");
                    } else {
                        Log.w(TAG, "Respuesta no exitosa o cuerpo nulo. Código HTTP: " + response.code());
                        if (response.errorBody() != null) {
                            Log.w(TAG, "Cuerpo del error: " + response.errorBody().string());
                        }
                        Toast.makeText(HomeActivity.this, "Error al cargar el menú", Toast.LENGTH_SHORT).show();
                        // Añadir solo "Cerrar Sesión" en caso de error
                        Menu menu = navigationView.getMenu();
                        menu.clear();
                        menu.add(Menu.NONE, R.id.nav_cerrar_sesion, Menu.NONE, "Cerrar Sesión")
                                .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                        Log.d(TAG, "Añadido solo 'Cerrar Sesión' por error en la respuesta");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error al procesar respuesta del menú: " + e.getMessage(), e);
                    Toast.makeText(HomeActivity.this, "Error al procesar el menú: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Añadir solo "Cerrar Sesión" en caso de excepción
                    Menu menu = navigationView.getMenu();
                    menu.clear();
                    menu.add(Menu.NONE, R.id.nav_cerrar_sesion, Menu.NONE, "Cerrar Sesión")
                            .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                    Log.d(TAG, "Añadido solo 'Cerrar Sesión' por excepción");
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                Log.e(TAG, "Fallo de conexión al cargar el menú: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Fallo de conexión al cargar el menú: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Añadir solo "Cerrar Sesión" en caso de fallo
                Menu menu = navigationView.getMenu();
                menu.clear();
                menu.add(Menu.NONE, R.id.nav_cerrar_sesion, Menu.NONE, "Cerrar Sesión")
                        .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                Log.d(TAG, "Añadido solo 'Cerrar Sesión' por fallo de conexión");
            }
        });
    }

    private int getMenuItemId(String nombreMenu) {
        if (nombreMenu == null) {
            Log.w(TAG, "Nombre de menú es null");
            return -1;
        }
        switch (nombreMenu.trim()) {
            case "Registrar Usuario":
                return R.id.nav_registrar_usuario;
            case "Listar Personas":
                return R.id.nav_listar_usuarios;
            case "Registrar Nota":
                return R.id.nav_registrar_nota;
            default:
                Log.w(TAG, "Nombre de menú no reconocido: " + nombreMenu);
                return -1;
        }
    }

    private int getMenuItemIcon(String nombreMenu) {
        if (nombreMenu == null) {
            Log.w(TAG, "Nombre de menú es null para ícono");
            return android.R.drawable.ic_menu_close_clear_cancel;
        }
        switch (nombreMenu.trim()) {
            case "Registrar Usuario":
                return android.R.drawable.ic_menu_add;
            case "Listar Personas":
                return android.R.drawable.ic_menu_view;
            case "Registrar Nota":
                return android.R.drawable.ic_menu_edit;
            default:
                Log.w(TAG, "Sin ícono para el menú: " + nombreMenu);
                return android.R.drawable.ic_menu_close_clear_cancel;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        try {
            if (id == R.id.nav_registrar_usuario) {
                startActivity(new Intent(this, RegistroUsuarioActivity.class));
                Log.d(TAG, "Navegando a RegistroUsuarioActivity");
            } else if (id == R.id.nav_listar_usuarios) {
                startActivity(new Intent(this, ListaPersonasActivity.class));
                Log.d(TAG, "Navegando a ListaPersonasActivity");
            } else if (id == R.id.nav_registrar_nota) {
                startActivity(new Intent(this, RegistrarNotaActivity.class));
                Log.d(TAG, "Navegando a RegistrarNotaActivity");
            } else if (id == R.id.nav_cerrar_sesion) {
                SharedPreferences prefs = getSharedPreferences("colegioAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                Log.d(TAG, "Sesión cerrada, navegando a MainActivity");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al seleccionar elemento del menú: " + e.getMessage(), e);
            Toast.makeText(this, "Error al navegar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}