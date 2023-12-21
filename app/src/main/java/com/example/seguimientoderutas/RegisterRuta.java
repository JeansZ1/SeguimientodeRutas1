package com.example.seguimientoderutas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterRuta extends AppCompatActivity {

    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;
    private static final int DESTINATION_ACTIVITY_REQUEST_CODE = 2;
    private boolean isRecording = false;
    private double selectedRouteLatitude = 0.0;
    private double selectedRouteLongitude = 0.0;
    private double selectedDestinationLatitude = 0.0;
    private double selectedDestinationLongitude = 0.0;

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
        editTextSelectDestination.setText("Seleccionar Destino");

        EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);
        editTextSelectRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterRuta.this, MapActivity.class);
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
            }
        });

        editTextSelectDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterRuta.this, MapActivity.class);
                startActivityForResult(intent, DESTINATION_ACTIVITY_REQUEST_CODE);
            }
        });

        buttonStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartRoute.setVisibility(View.GONE);
                buttonStopRoute.setVisibility(View.VISIBLE);
                isRecording = true;
                Toast.makeText(RegisterRuta.this, "Registrando ruta...", Toast.LENGTH_SHORT).show();

                EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
                String destination = editTextSelectDestination.getText().toString().trim();

                if (!destination.equals("Seleccionar Destino") && selectedRouteLatitude != 0.0 && selectedRouteLongitude != 0.0 &&
                        selectedDestinationLatitude != 0.0 && selectedDestinationLongitude != 0.0) {

                    guardarRutaEnFirebase(selectedDestinationLatitude, selectedDestinationLongitude,
                            selectedRouteLatitude, selectedRouteLongitude);

                } else {
                    Toast.makeText(RegisterRuta.this, "Por favor, selecciona destino y ruta", Toast.LENGTH_SHORT).show();
                }
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
            EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);
            editTextSelectRoute.setText(location);

            selectedRouteLatitude = selectedLatitude;
            selectedRouteLongitude = selectedLongitude;
        } else if (requestCode == DESTINATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            double selectedLatitude = data.getDoubleExtra("selectedLatitude", 0.0);
            double selectedLongitude = data.getDoubleExtra("selectedLongitude", 0.0);

            String location = "Latitud: " + selectedLatitude + ", Longitud: " + selectedLongitude;
            EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
            editTextSelectDestination.setText(location);

            selectedDestinationLatitude = selectedLatitude;
            selectedDestinationLongitude = selectedLongitude;
        }
    }

    private void guardarRutaEnFirebase(double selectedDestinationLatitude, double selectedDestinationLongitude,
                                       double selectedRouteLatitude, double selectedRouteLongitude) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference routesRef = database.getReference("Rutas");

        String nuevaRutaKey = routesRef.push().getKey();

        Route nuevaRuta = new Route();
        nuevaRuta.setPuntoPartida("Latitud: " + selectedRouteLatitude + ", Longitud: " + selectedRouteLongitude);
        nuevaRuta.setPuntoLlegada("Latitud: " + selectedDestinationLatitude + ", Longitud: " + selectedDestinationLongitude);

        String fechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
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
