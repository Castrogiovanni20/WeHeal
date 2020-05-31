package com.example.weheal;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderNotificaciones extends RecyclerView.ViewHolder {

    private FirebaseDatabase db;
    private DatabaseReference reference;
    private View view;

    public ViewHolderNotificaciones(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setDetails(Context context, final Notificacion notificacion){
        final TextView title = view.findViewById(R.id.rTitleView);
        final TextView description = view.findViewById(R.id.rDescription);
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
                        Picasso.get().load(notificacion.getPhoto_postulant()).into(imgProfile);
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
                        description.setText(name + ", tu solicitud de " + ds.child("name").getValue().toString() + " fue rechazada por el dueño del insumo. Lo lamentamos!");
                        Picasso.get().load(notificacion.getPhoto_owner()).into(imgProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
