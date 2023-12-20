package com.example.seguimientoderutas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseReference routesRef;
    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        routesRef = database.getReference("Rutas");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter para mostrar las rutas
        routeAdapter = new RouteAdapter();
        recyclerView.setAdapter(routeAdapter);

        // Obtener y mostrar las rutas almacenadas en Firebase
        getRoutesFromFirebase();
    }

    private void getRoutesFromFirebase() {
        routesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Route> routeList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Route route = snapshot.getValue(Route.class);
                    if (route != null) {
                        routeList.add(route);
                    }
                }

                // Actualizar el RecyclerView con las rutas obtenidas
                routeAdapter.setRoutes(routeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores si la lectura de datos falla
                Toast.makeText(HistoryActivity.this, "Error al obtener rutas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
