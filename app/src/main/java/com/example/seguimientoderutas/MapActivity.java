package com.example.seguimientoderutas;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker selectedMarker;
    private LatLng origin;
    private LatLng destination;
    private Polyline routePolyline;

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
                    returnIntent.putExtra("selectedLatitude", latitude);
                    returnIntent.putExtra("selectedLongitude", longitude);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        // Establecer origen y destino desde los extras o intentos
        if (getIntent().hasExtra("origin") && getIntent().hasExtra("destination")) {
            origin = getIntent().getParcelableExtra("origin");
            destination = getIntent().getParcelableExtra("destination");
            drawRoute(origin, destination);
        }
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

    private void drawRoute(LatLng origin, LatLng destination) {
        // Utilizar la API de direcciones de Google Maps para obtener la ruta entre los puntos

        // Crear una instancia de GeoApiContext con tu clave de API de Google Maps
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCSM-98gpW4_KGOvu0IRd69QdyS7-CdbtE")
                .build();

        // Crear una solicitud para obtener la ruta entre el origen y el destino
        com.google.maps.model.LatLng googleOrigin = new com.google.maps.model.LatLng(origin.latitude, origin.longitude);
        com.google.maps.model.LatLng googleDestination = new com.google.maps.model.LatLng(destination.latitude, destination.longitude);

        DirectionsApi.getDirections(context, googleOrigin.toString(), googleDestination.toString())
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        if (result != null && result.routes.length > 0) {
                            // Obtener los puntos de la ruta y trazarla en el mapa
                            List<LatLng> routePoints = new ArrayList<>();

                            for (DirectionsStep step : result.routes[0].legs[0].steps) {
                                List<com.google.maps.model.LatLng> polyline = step.polyline.decodePath();

                                for (com.google.maps.model.LatLng coords : polyline) {
                                    routePoints.add(new LatLng(coords.lat, coords.lng));
                                }
                            }

                            // Dibujar la polilínea en el mapa
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PolylineOptions polylineOptions = new PolylineOptions()
                                            .addAll(routePoints)
                                            .width(8)
                                            .color(getResources().getColor(R.color.colorPrimary));

                                    routePolyline = mMap.addPolyline(polylineOptions);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        // Manejar el fallo en la obtención de la ruta
                        if (e != null) {
                            // Puedes mostrar un mensaje de error o registrar la excepción
                            e.printStackTrace();
                            // Por ejemplo, mostrar un Toast con el mensaje de error
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapActivity.this, "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }

}