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

    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ruta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);
        String location = "Latitud: " + latitude + ", Longitud: " + longitude;
        editTextSelectDestination.setText(location);

        // Cambiado: Utilizando EditText en lugar de Button
        EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);
        editTextSelectRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para seleccionar la ruta aquí
                Intent intent = new Intent(RegisterRuta.this, MapActivity.class);
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
            }
        });

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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            double selectedLatitude = data.getDoubleExtra("selectedLatitude", 0.0);
            double selectedLongitude = data.getDoubleExtra("selectedLongitude", 0.0);

            String location = "Latitud: " + selectedLatitude + ", Longitud: " + selectedLongitude;
            EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
            editTextSelectDestination.setText(location);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String fechaHora = dateFormat.format(new Date());

            guardarRutaEnFirebase(selectedLatitude, selectedLongitude, fechaHora);
        }
    }

    private void guardarRutaEnFirebase(double selectedLatitude, double selectedLongitude, String fechaHora) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference routesRef = database.getReference("Rutas");

        String nuevaRutaKey = routesRef.push().getKey();

        Route nuevaRuta = new Route();
        nuevaRuta.setPuntoPartida("Punto de partida");
        // Utiliza las coordenadas de llegada proporcionadas
        nuevaRuta.setPuntoLlegada("Latitud: " + selectedLatitude + ", Longitud: " + selectedLongitude);
        nuevaRuta.setFechaHora(fechaHora);

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
