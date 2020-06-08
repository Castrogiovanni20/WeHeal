package com.example.weheal;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DatosSolicitante extends AppCompatActivity {

    private final String TAG = "DatosSolicitante";
    private CircleImageView fotoPerfil;
    private TextView nombre;
    private Button entregeInsumo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_solicitante);

        String uriPerfil = getIntent().getExtras().getString("photoPostulante");
        String nombrePostualnte = getIntent().getExtras().getString("nombrePostulante");
        final String idInsumo = getIntent().getExtras().getString("idInsumo");
        final String idNotificacion = getIntent().getExtras().getString("idNotificacion");
        final String idPostulante = getIntent().getExtras().getString("idPostulante");

        fotoPerfil = findViewById(R.id.imagenPerfil);
        Picasso.get().load(uriPerfil).into(fotoPerfil);

        nombre = findViewById(R.id.nombre);
        nombre.setText(nombrePostualnte);

        entregeInsumo = findViewById(R.id.entregeInsumo);
        entregeInsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //eliminarInsumo(idInsumo);
                eliminarNotificacion(idNotificacion);
                enviarNotificacionAPostulante(idPostulante, idInsumo);
                Intent intent = new Intent(getApplicationContext(), AnimationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void eliminarInsumo(String idInsumo){
        Query firebaseQuery = FirebaseDatabase.getInstance().getReference("Insumos").orderByKey().equalTo(idInsumo);
        firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void eliminarNotificacion(String idNotificacion){
        Query firebaseQuery = FirebaseDatabase.getInstance().getReference("Notificaciones").orderByKey().equalTo(idNotificacion);
        firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void enviarNotificacionAPostulante(String idPostulante, String idInsumo){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Notificaciones");
        final String STATE = "Info";
        final String photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("destination", idPostulante);
        notificacion.put("state", STATE);
        notificacion.put("photo", photo);
        notificacion.put("id_medical_input", idInsumo);

        db.push().setValue(notificacion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Notificacion enviada con exito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ViewHolder", "Ocurrio un error al enviar la notificacion");
                    }
                });
    }
}
