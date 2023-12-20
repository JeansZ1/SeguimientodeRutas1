package com.example.seguimientoderutas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private List<Route> routeList;

    public RouteAdapter() {
        // Constructor vacío inicializando la lista de rutas
    }

    public void setRoutes(List<Route> routeList) {
        this.routeList = routeList;
        notifyDataSetChanged(); // Actualizar la vista del RecyclerView
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        if (routeList != null && position < routeList.size()) {
            Route route = routeList.get(position);

            // Actualizar la vista del elemento de la ruta
            holder.bindRoute(route);
        }
    }

    @Override
    public int getItemCount() {
        return routeList != null ? routeList.size() : 0;
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPuntoPartida;
        private TextView textViewPuntoLlegada;
        private TextView textViewFechaHora;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPuntoPartida = itemView.findViewById(R.id.textViewPuntoPartida);
            textViewPuntoLlegada = itemView.findViewById(R.id.textViewPuntoLlegada);
            textViewFechaHora = itemView.findViewById(R.id.textViewFechaHora);
        }

        public void bindRoute(Route route) {
            // Mostrar los detalles de la ruta en los TextView correspondientes
            textViewPuntoPartida.setText(route.getPuntoPartida());
            textViewPuntoLlegada.setText(route.getPuntoLlegada());
            textViewFechaHora.setText(route.getFechaHora());
        }
    }
}
