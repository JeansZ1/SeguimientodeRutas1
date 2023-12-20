package com.example.seguimientoderutas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Button buttonRegistrar = findViewById(R.id.buttonRegistrarRuta);
        Button buttonHistoria = findViewById(R.id.buttonHistoria);
        Button buttonMapa = findViewById(R.id.buttonMapa);

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al presionar el botón "Registrar" independiente de RegisterActivity
                // Por ejemplo, mostrar un mensaje
                Toast.makeText(MenuActivity.this, "Acción de Registro Personalizado", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al presionar el botón "Registrar Ruta"
                // Por ejemplo, iniciar RegisterRuta Activity
                startActivity(new Intent(MenuActivity.this, RegisterRuta.class));
            }
        });

        buttonHistoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al presionar el botón "Historia"
                // Por ejemplo, mostrar la actividad de historial de rutas
                startActivity(new Intent(MenuActivity.this, HistoryActivity.class));
            }
        });

        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al presionar el botón "Mapa"
                // Por ejemplo, mostrar la actividad del mapa
                startActivity(new Intent(MenuActivity.this, MapActivity.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpiar la pila de actividades
            startActivity(intent);
            finish();

            Toast.makeText(this, "Usuario desconectado", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}