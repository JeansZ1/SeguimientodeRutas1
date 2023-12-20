package com.example.seguimientoderutas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterRuta extends AppCompatActivity {

    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ruta);

        // Configurar Toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar el botón de navegación hacia atrás
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button buttonStartRoute = findViewById(R.id.buttonStartRoute);
        Button buttonStopRoute = findViewById(R.id.buttonStopRoute);

        buttonStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartRoute.setVisibility(View.GONE);
                buttonStopRoute.setVisibility(View.VISIBLE);
                isRecording = true;
                Toast.makeText(RegisterRuta.this, "Registrando ruta...", Toast.LENGTH_SHORT).show();
                // Lógica para iniciar la grabación de la ruta (puedes integrar aquí la captura de coordenadas y Firebase)
            }
        });

        buttonStopRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStopRoute.setVisibility(View.GONE);
                buttonStartRoute.setVisibility(View.VISIBLE);
                isRecording = false;
                Toast.makeText(RegisterRuta.this, "Registro de ruta detenido", Toast.LENGTH_SHORT).show();

                // Obtiene la fecha y hora actual
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fechaHora = dateFormat.format(date);

                // Puedes obtener el punto de llegada de alguna manera, por ejemplo, si se selecciona desde MapActivity
                String puntoLlegada = "Tu punto de llegada seleccionado";

                guardarRutaEnFirebase(puntoLlegada, fechaHora);
            }
        });

        // Manejo del clic en editTextSelectDestination
        EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
        editTextSelectDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia MapActivity para seleccionar la ubicación
                Intent intent = new Intent(RegisterRuta.this, MapActivity.class);
                startActivity(intent);
            }
        });

        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
        String location = "Latitud: " + latitude + ", Longitud: " + longitude;
        editTextSelectDestination.setText(location);
    }

    private void guardarRutaEnFirebase(String puntoLlegada, String fechaHora) {
        // Obtener la referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference routesRef = database.getReference("Rutas");

        // Generar una clave única para la nueva ruta
        String nuevaRutaKey = routesRef.push().getKey();

        // Crear un objeto Route con los detalles de la ruta
        Route nuevaRuta = new Route();
        nuevaRuta.setPuntoPartida("Punto de partida");
        nuevaRuta.setPuntoLlegada(puntoLlegada);
        nuevaRuta.setFechaHora(fechaHora);

        // Guardar la nueva ruta en la base de datos en la ubicación "Rutas/ID_Ruta_generada"
        routesRef.child(nuevaRutaKey).setValue(nuevaRuta)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterRuta.this, "Ruta guardada en Firebase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterRuta.this, "Error al guardar la ruta en Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
