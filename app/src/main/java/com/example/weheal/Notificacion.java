package com.example.weheal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Notificacion {
    private String id_medical_input;
    private String postulant;
    private String name_postulant;
    private String photo;
    private String destination;
    private String imagenPerfil;
    private String titulo;
    private String descripcion;
    private String state;

    public Notificacion(){
    }

    public String getid_medical_input() {
        return id_medical_input;
    }

    public void setid_medical_input(String ID) {
        this.id_medical_input = ID;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getTitulo() {
        if (state.equalsIgnoreCase("Waiting")){
            titulo = "Solicitud de donacion ";
        }
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String description) {
        if (state.equalsIgnoreCase("Waiting")){
            descripcion = description;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName_postulant() {
        return name_postulant;
    }

    public void setName_postulant(String nombre_solicitante) {
        this.name_postulant = nombre_solicitante;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPostulant() {
        return postulant;
    }

    public void setPostulant(String postulant) {
        this.postulant = postulant;
    }
}
