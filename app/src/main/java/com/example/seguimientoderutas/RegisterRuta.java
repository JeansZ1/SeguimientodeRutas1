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

        EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);
        editTextSelectRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
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
            // Manejar el resultado del mapa para la ubicación actual (editTextSelectRoute)
            double selectedLatitude = data.getDoubleExtra("selectedLatitude", 0.0);
            double selectedLongitude = data.getDoubleExtra("selectedLongitude", 0.0);

            String location = "Latitud: " + selectedLatitude + ", Longitud: " + selectedLongitude;
            EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);
            editTextSelectRoute.setText(location);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String fechaHora = dateFormat.format(new Date());

            guardarRutaEnFirebase(selectedLatitude, selectedLongitude, fechaHora);
        } else if (requestCode == DESTINATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Manejar el resultado del mapa para la selección del destino (editTextSelectDestination)
            double selectedLatitude = data.getDoubleExtra("selectedLatitude", 0.0);
            double selectedLongitude = data.getDoubleExtra("selectedLongitude", 0.0);

            String location = "Latitud: " + selectedLatitude + ", Longitud: " + selectedLongitude;
            EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
            editTextSelectDestination.setText(location);
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(RegisterRuta.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RegisterRuta.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterRuta.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (lastKnownLocation != null) {
            double currentLatitude = lastKnownLocation.getLatitude();
            double currentLongitude = lastKnownLocation.getLongitude();

            Intent intent = new Intent(RegisterRuta.this, MapActivity.class);
            intent.putExtra("currentLatitude", currentLatitude);
            intent.putExtra("currentLongitude", currentLongitude);
            startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
        } else {
            Toast.makeText(RegisterRuta.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
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
