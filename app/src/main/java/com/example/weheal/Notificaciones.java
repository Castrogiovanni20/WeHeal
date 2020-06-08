package com.example.weheal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private BottomNavigationView nav;
    private RecyclerView mRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<Notificacion, ViewHolderNotificaciones> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Notificaciones");

        nav = findViewById(R.id.bottom_navigation);
        nav.setBackgroundColor(Color.parseColor("#ffffff"));
        nav.setSelectedItemId(R.id.nav_notifications);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        return true;
                    case R.id.nav_addMedicacion:
                        startActivity(new Intent(getApplicationContext(), CargarMedicacion.class));
                        return true;
                    case R.id.nav_notifications:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Query firebaseQuery = reference.orderByChild("destination").equalTo(idUser);
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Notificacion, ViewHolderNotificaciones>(Notificacion.class, R.layout.card_notificacion, ViewHolderNotificaciones.class, firebaseQuery) {
                    @Override
                    protected void populateViewHolder(ViewHolderNotificaciones viewHolderNotificaciones, Notificacion notificacion, int i) {
                        String key = firebaseRecyclerAdapter.getRef(i).getKey();
                        viewHolderNotificaciones.setDetails(getApplicationContext(), notificacion);
                        viewHolderNotificaciones.setActions(getApplicationContext(), notificacion, key);
                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
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
                    } else {
                        removeItemFirebase(notification);
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
}
