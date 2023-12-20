package com.example.seguimientoderutas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnSaveLocation = findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMarker != null) {
                    LatLng selectedLocation = selectedMarker.getPosition();
                    double latitude = selectedLocation.latitude;
                    double longitude = selectedLocation.longitude;

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("selectedLocation", selectedLocation);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Establecer un listener para el clic en el mapa
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Eliminar el marcador existente si hay alguno
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }

                // Añadir un nuevo marcador en la ubicación seleccionada
                selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));

                // Mover la cámara a la ubicación seleccionada
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }
}
