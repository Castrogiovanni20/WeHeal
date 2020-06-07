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
                        description.setText(notificacion.getName_postulant() + " solicito la donacion de " + ds.child("name").getValue().toString());
                        Picasso.get().load(notificacion.getPhoto()).into(imgProfile);
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
        }
    }

    public void setActions(final Context context, final Notificacion notification, final String key){

        if (notification.getState().equalsIgnoreCase("Waiting")){
            final TextView aceptarDonacion = view.findViewById(R.id.rAccion);
            aceptarDonacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }

    public void cambiarEstado(Notificacion notification, final String key){
        final String STATE = "Accepted";
        Query firebaseQuery = FirebaseDatabase.getInstance().getReference("Notificaciones").orderByKey().equalTo(key);
        firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    FirebaseDatabase.getInstance().getReference("Notificaciones").child(key).child("state").setValue(STATE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
