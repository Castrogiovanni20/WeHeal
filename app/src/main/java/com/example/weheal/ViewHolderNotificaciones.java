package com.example.weheal;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
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
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderNotificaciones extends RecyclerView.ViewHolder {

    private FirebaseDatabase db;
    private DatabaseReference reference;
    private View view;

    public ViewHolderNotificaciones(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setDetails(final Context context, final Notificacion notificacion){
        final TextView title = view.findViewById(R.id.rTitleView);
        final TextView description = view.findViewById(R.id.rDescription);
        final TextView accion = view.findViewById(R.id.rAccion);
        final CircleImageView imgProfile = view.findViewById(R.id.profile_image);

        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Insumos");

        if (notificacion.getState().equalsIgnoreCase("Waiting")){

            Query firebaseQuery = reference.orderByKey().equalTo(notificacion.getid_medical_input());

            firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        title.setText(notificacion.getTitulo());
                        description.setText(notificacion.getName_postulant() + " solicito la donación de " + ds.child("name").getValue().toString());
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
                        accion.setText("Aceptar donación");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (notificacion.getState().equalsIgnoreCase("Declined")){
            final String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Insumos");
            Query firebaseQuery = reference.orderByKey().equalTo(notificacion.getid_medical_input());

            firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        title.setText("Solicitud rechazada");
                        description.setText(name + ", tu solicitud de " + ds.child("name").getValue().toString() + " fue rechazada.");
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
                        accion.setVisibility(View.INVISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (notificacion.getState().equalsIgnoreCase("Accepted")){

            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Insumos");
            Query firebaseQuery = reference.orderByKey().equalTo(notificacion.getid_medical_input());

            firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        title.setText("¡Donaste " + ds.child("name").getValue().toString() + "!");
                        description.setText("Coordina con el solicitante la entrega del insumo.");
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
                        accion.setText("Ver detalles");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (notificacion.getState().equalsIgnoreCase("Info")){

            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Insumos");
            Query firebaseQuery = reference.orderByKey().equalTo(notificacion.getid_medical_input());

            firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        title.setText("¡Recibiste el insumo!");
                        description.setText("El dueño nos confirmo que recibiste " + ds.child("name").getValue().toString());
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
                        accion.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (notificacion.getState().equalsIgnoreCase("Alert")){
            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Insumos");
            Query firebaseQuery = reference.orderByKey().equalTo(notificacion.getid_medical_input());

            firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        title.setText("Donacion aceptada");
                        description.setText("El dueño se pondra en contacto contigo para entregarte " + ds.child("name").getValue().toString());
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
                        accion.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (notificacion.getState().equalsIgnoreCase("Confirmation")){
            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Insumos");
            Query firebaseQuery = reference.orderByKey().equalTo(notificacion.getid_medical_input());
            firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        title.setText("Solicitaste " + ds.child("name").getValue().toString());
                        description.setText("El dueño se pondra en contacto en caso de aceptar la solicitud");
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
                        accion.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void setActions(final Context context, final Notificacion notification, final String key){

        if (notification.getState().equalsIgnoreCase("Waiting")){
            final TextView aceptarDonacion = view.findViewById(R.id.rAccion);
            aceptarDonacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviarPushASolicitante(context, notification.getPostulant());
                    enviarNotificacionASolicitante(notification);
                    cambiarEstado(notification, key);
                    Intent intent = new Intent(context, AnimationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        } else if (notification.getState().equalsIgnoreCase("Accepted")){
            final TextView verDetalles = view.findViewById(R.id.rAccion);
            verDetalles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DatosSolicitante.class);
                    intent.putExtra("idInsumo", notification.getid_medical_input());
                    intent.putExtra("idNotificacion", key);
                    intent.putExtra("idPostulante", notification.getPostulant());
                    intent.putExtra("nombrePostulante", notification.getName_postulant());
                    intent.putExtra("photoPostulante", notification.getPhoto());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }

    public void cambiarEstado(Notificacion notification, final String key){
        final String STATE = "Accepted";
        FirebaseDatabase.getInstance().getReference("Notificaciones").child(key).child("state").setValue(STATE);
    }

    public void enviarPushASolicitante(Context context, String idPostulante){
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

                        notificacion.put("title", "Donacion aceptada");
                        notificacion.put("body",  nombreDuenio + " se pondra en contacto contigo para coordinar la entrega del insumo");

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

    public void enviarNotificacionASolicitante(Notificacion notification){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Notificaciones");
        final String STATE = "Alert";
        final String photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("destination", notification.getPostulant());
        notificacion.put("state", STATE);
        notificacion.put("photo", photo);
        notificacion.put("id_medical_input", notification.getid_medical_input());

        db.push().setValue(notificacion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ViewHolderNotif", "Notificacion enviada con exito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ViewHolderNotif", "Ocurrio un error al enviar la notificacion");
                    }
                });


    }


}
