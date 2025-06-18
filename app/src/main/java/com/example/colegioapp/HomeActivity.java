package com.example.colegioapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView mensaje = findViewById(R.id.tvMensaje);
        mensaje.setText("Bienvenido al sistema escolar");
    }
}
