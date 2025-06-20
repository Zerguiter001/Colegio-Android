package com.example.colegioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder> {

    private List<Persona> personaList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Persona persona);
    }

    public PersonaAdapter(List<Persona> personaList, OnItemClickListener listener) {
        this.personaList = personaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona, parent, false);
        return new PersonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaViewHolder holder, int position) {
        Persona persona = personaList.get(position);
        holder.textViewNombre.setText(persona.getNombres() + " " + persona.getApellidos());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(persona));
    }

    @Override
    public int getItemCount() {
        return personaList.size();
    }

    static class PersonaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;

        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
        }
    }
}