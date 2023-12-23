package com.example.seguimientoderutas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setEnabled(false); // Inicialmente deshabilitar el botón Limpiar

        // Restaurar el estado del botón 'Detener Ruta' desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("EstadoRegistro", Context.MODE_PRIVATE);
        isRecording = sharedPreferences.getBoolean("isRecording", false);

        if (isRecording) {
            buttonStartRoute.setVisibility(View.GONE);
            buttonStopRoute.setVisibility(View.VISIBLE);
            // Habilitar el botón de limpiar si la grabación está activa
            buttonClear.setEnabled(true);
        } else {
            buttonStopRoute.setVisibility(View.GONE);
            buttonStartRoute.setVisibility(View.VISIBLE);
        }

        EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
        EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);

        editTextSelectDestination.setText("Seleccionar Destino");

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
                buttonClear.setEnabled(true); // Habilitar el botón Limpiar
                Toast.makeText(RegisterRuta.this, "Registrando ruta...", Toast.LENGTH_SHORT).show();

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

                // Habilitar el botón de limpiar
                buttonClear.setEnabled(true);
            }
        });

        // Restaurar las ubicaciones desde SharedPreferences
        SharedPreferences ubicacionesSharedPreferences = getSharedPreferences("Ubicaciones", Context.MODE_PRIVATE);
        selectedRouteLatitude = Double.longBitsToDouble(ubicacionesSharedPreferences.getLong("routeLatitude", 0));
        selectedRouteLongitude = Double.longBitsToDouble(ubicacionesSharedPreferences.getLong("routeLongitude", 0));
        selectedDestinationLatitude = Double.longBitsToDouble(ubicacionesSharedPreferences.getLong("destinationLatitude", 0));
        selectedDestinationLongitude = Double.longBitsToDouble(ubicacionesSharedPreferences.getLong("destinationLongitude", 0));

        // Actualizar los EditText con las ubicaciones restauradas
        editTextSelectRoute.setText("Latitud: " + selectedRouteLatitude + ", Longitud: " + selectedRouteLongitude);
        editTextSelectDestination.setText("Latitud: " + selectedDestinationLatitude + ", Longitud: " + selectedDestinationLongitude);

        // Agregar TextChangedListeners para habilitar/deshabilitar el botón Limpiar según el contenido de los EditText
        editTextSelectRoute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() || !editTextSelectDestination.getText().toString().isEmpty()) {
                    buttonClear.setEnabled(true);
                } else {
                    buttonClear.setEnabled(false);
                }
            }
        });

        editTextSelectDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() || !editTextSelectRoute.getText().toString().isEmpty()) {
                    buttonClear.setEnabled(true);
                } else {
                    buttonClear.setEnabled(false);
                }
            }
        });

        // Agregar funcionalidad al botón Limpiar
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
                // Deshabilitar el botón después de limpiar
                buttonClear.setEnabled(false);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Guardar el estado actual de la grabación en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("EstadoRegistro", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRecording", isRecording);
        editor.apply();
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

            // Guardar las ubicaciones en SharedPreferences
            guardarUbicacionesEnSharedPreferences(selectedRouteLatitude, selectedRouteLongitude,
                    selectedDestinationLatitude, selectedDestinationLongitude);
        } else if (requestCode == DESTINATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            double selectedLatitude = data.getDoubleExtra("selectedLatitude", 0.0);
            double selectedLongitude = data.getDoubleExtra("selectedLongitude", 0.0);

            String location = "Latitud: " + selectedLatitude + ", Longitud: " + selectedLongitude;
            EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
            editTextSelectDestination.setText(location);

            selectedDestinationLatitude = selectedLatitude;
            selectedDestinationLongitude = selectedLongitude;

            // Guardar las ubicaciones en SharedPreferences
            guardarUbicacionesEnSharedPreferences(selectedRouteLatitude, selectedRouteLongitude,
                    selectedDestinationLatitude, selectedDestinationLongitude);
        }
    }

    private void guardarUbicacionesEnSharedPreferences(double routeLatitude, double routeLongitude,
                                                       double destinationLatitude, double destinationLongitude) {
        // Guardar las ubicaciones en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Ubicaciones", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("routeLatitude", Double.doubleToRawLongBits(routeLatitude));
        editor.putLong("routeLongitude", Double.doubleToRawLongBits(routeLongitude));
        editor.putLong("destinationLatitude", Double.doubleToRawLongBits(destinationLatitude));
        editor.putLong("destinationLongitude", Double.doubleToRawLongBits(destinationLongitude));
        editor.apply();
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

    private void limpiarCampos() {
        EditText editTextSelectDestination = findViewById(R.id.editTextSelectDestination);
        EditText editTextSelectRoute = findViewById(R.id.editTextSelectRoute);

        editTextSelectDestination.setText("Seleccionar Destino");
        editTextSelectRoute.setText("");

        // Limpiar las ubicaciones guardadas en SharedPreferences
        guardarUbicacionesEnSharedPreferences(0.0, 0.0, 0.0, 0.0);
    }
}
