package com.example.weheal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Notificaciones extends AppCompatActivity {

    private AHBottomNavigation bottomNavigation;
    private RecyclerView mRecyclerView;
    private TextView textoNotificacion;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<Notificacion, ViewHolderNotificaciones> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        getSupportActionBar().setTitle("Notificaciones");

        textoNotificacion = findViewById(R.id.texto_notificaciones);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Notificaciones");


        bottomNavigation = findViewById(R.id.bottom_navigation);
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Add", R.drawable.ic_add_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications_none_black_24dp);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#6200EE"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setCurrentItem(2);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        return true;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), CargarMedicacion.class));
                        return true;
                    case 2:
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        listarNotificaciones();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deleteNotification(firebaseRecyclerAdapter.getRef(position));
        }

        public void deleteNotification(final DatabaseReference notification){
            notification.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("state").getValue().toString().equalsIgnoreCase("Waiting")){
                        String destination = dataSnapshot.child("postulant").getValue().toString();
                        String id_medical_input = dataSnapshot.child("id_medical_input").getValue().toString();
                        newNotification(destination, id_medical_input);
                        removeItemFirebase(notification);
                        actualizarBadgeNotificaciones(-1);
                        listarNotificaciones();
                    } else {
                        removeItemFirebase(notification);
                        actualizarBadgeNotificaciones(-1);
                        listarNotificaciones();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

            public void removeItemFirebase(DatabaseReference notificacion){
                notificacion.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Notificaciones", "Elemento eliminado con exito!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Notificaciones", "Ocurrio un error al eliminar el elemento");
                    }
                });

            }

            public void newNotification(String destination, String id_medical_input){
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Notificaciones");
                final String photo_owner = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

                Log.d("Notificacion", "destino:" + destination);
                String state = "Declined";

                Map<String, Object> notificacion = new HashMap<>();
                notificacion.put("state",state);
                notificacion.put("destination", destination);
                notificacion.put("id_medical_input", id_medical_input);
                notificacion.put("photo", photo_owner);
                db.push().setValue(notificacion);
            }
    };

    public void listarNotificaciones(){
        final String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Query firebaseQuery = reference.orderByChild("destination").equalTo(idUser);
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Notificacion, ViewHolderNotificaciones>(Notificacion.class, R.layout.card_notificacion, ViewHolderNotificaciones.class, firebaseQuery) {
                    @Override
                    protected void populateViewHolder(ViewHolderNotificaciones viewHolderNotificaciones, Notificacion notificacion, int i) {
                        String key = firebaseRecyclerAdapter.getRef(i).getKey();
                        viewHolderNotificaciones.setDetails(getApplicationContext(), notificacion);
                        viewHolderNotificaciones.setActions(getApplicationContext(), notificacion, key);
                        actualizarBadgeNotificaciones(i);
                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void actualizarBadgeNotificaciones(int cant){
        if (cant != -1){
            AHNotification notification = new AHNotification.Builder()
                    .setText(String.valueOf(cant + 1))
                    .setBackgroundColor(ContextCompat.getColor(Notificaciones.this, R.color.background_notification))
                    .setTextColor(ContextCompat.getColor(Notificaciones.this, R.color.text_notification))
                    .build();
            bottomNavigation.setNotification(notification, 2);
            textoNotificacion.setVisibility(View.INVISIBLE);
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText("")
                    .setBackgroundColor(ContextCompat.getColor(Notificaciones.this, R.color.text_notification))
                    .setTextColor(ContextCompat.getColor(Notificaciones.this, R.color.text_notification))
                    .build();
            bottomNavigation.setNotification(notification, 2);
        }
    }
}
