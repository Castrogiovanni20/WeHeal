package com.example.weheal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DatosSolicitante extends AppCompatActivity {

    private final String TAG = "DatosSolicitante";
    private CircleImageView fotoPerfil;
    private TextView nombre;
    private Button entregeInsumo;
    private ImageView email;

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
                enviarPushAPostulante(getApplicationContext(), idPostulante);
                Intent intent = new Intent(getApplicationContext(), AnimationActivity.class);
                startActivity(intent);
            }
        });

        email = findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query nombreInsumoQuery = FirebaseDatabase.getInstance().getReference("Insumos").orderByKey().equalTo(idInsumo);
                final Query emailQuery = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("id").equalTo(idPostulante);

                nombreInsumoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot ds : dataSnapshot.getChildren()){
                            final String nombreInsumo = ds.child("name").getValue().toString();

                            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot ds : dataSnapshot.getChildren()){
                                        String emailPostulante = ds.child("email").getValue().toString();

                                        Intent intentMail = new Intent(Intent.ACTION_SEND);
                                        intentMail.setType("message/rfc822");
                                        intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{emailPostulante }); 
                                        intentMail.putExtra(Intent.EXTRA_SUBJECT, "Coordinar la entrega de " + nombreInsumo);

                                        startActivity(Intent.createChooser(intentMail, "Message to User to do what next"));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }


    /**
     * Eliminar un insumo de la DB de Firebase
     * @param idInsumo El ID del insumo a eliminar
     */
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


    /**
     * @description Eliminar una notificacion de la DB de Firebase
     * @param idNotificacion El ID de la notificacion a eliminar
     */
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


    /**
     * @description Enviar una notificacion al postulante de que recibio el insumo
     * @param idPostulante El ID del postulante
     * @param idInsumo El ID del insumo
     */
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
                        Log.d(TAG, "Ocurrio un error al enviar la notificacion");
                    }
                });
    }


    /**
     * @description Enviar una push al postulante de que recibio el insumo
     * @param context
     * @param idPostulante El ID del postulante
     */
    public void enviarPushAPostulante(Context context, String idPostulante){
        final RequestQueue myRequest = Volley.newRequestQueue(context);
        final JSONObject json = new JSONObject();

        Query tokenDestinoQuery  = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("id").equalTo(idPostulante);
        tokenDestinoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String tokenDestino = ds.child("token").getValue().toString();
                    String nombreDuenio = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                    try {
                        json.put("to", tokenDestino);
                        JSONObject notificacion = new JSONObject();

                        notificacion.put("title", "Recibiste el insumo");
                        notificacion.put("body",  nombreDuenio + " nos confirmo que recibiste el insumo");

                        json.put("data", notificacion);
                        String URL = "https://fcm.googleapis.com/fcm/send";

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, json, null, null){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization","key=AAAAG3ZP9Yw:APA91bF_IJd3q8QjWvnPpP1tLl_pv9aal4KrXloZu87FS_xkpSJgA6gxvBsPq6Sjq_IN5Ro2pmAhMj6_IeHn-R5HIhoJkStJEDK8KPdX9l8M7yyJcCquNHe1VtotFnKWToDJbLt_uBAH");
                                return header;
                            }
                        };

                        myRequest.add(request);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
