package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.login.LoginManager;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    ImageButton search;
    EditText searchField;
    TextView textBienvenida;
    BottomNavigationView nav;
    RecyclerView mRecyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    LottieAnimationView loading;
    ShimmerFrameLayout mShimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        handleSession();

        search = findViewById(R.id.search);
        searchField = findViewById(R.id.search_field);

        loading = findViewById(R.id.loading);
        mShimmer = findViewById(R.id.shimmer_view_container);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Insumos");

        guardarDatosUsuarioFirebase();

        nav = findViewById(R.id.bottom_navigation);
        nav.setBackgroundColor(Color.parseColor("#ffffff"));
        nav.setSelectedItemId(R.id.nav_home);

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_addMedicacion:
                        startActivity(new Intent(getApplicationContext(), CargarMedicacion.class));
                        return true;
                    case R.id.nav_notifications:
                        startActivity(new Intent(getApplicationContext(), Notificaciones.class));
                }
                return false;
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0); // Cierra el el teclado
                firebaseSearch(searchField.getText().toString());
                mostrarAnimacionLoading();
                loading.playAnimation();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        final String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        final FirebaseRecyclerAdapter<Insumo, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Insumo, ViewHolder>(Insumo.class, R.layout.card_insumo, ViewHolder.class, reference) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Insumo insumo, final int i) {
                        mShimmer.startShimmer();

                        final Object TAG = getRef(i).getKey();
                        final String insumoID = getRef(i).getKey();

                        viewHolder.setDetails(getApplicationContext(), insumoID, insumo.getName(), insumo.getImage(), insumo.getDescription(), insumo.getQuantity(), insumo.getOwner_photo());
                        viewHolder.setActions(getApplicationContext(), TAG, insumoID, user, insumo.getOwner(), photo, name, insumo.getName());

                        mShimmer.stopShimmer();
                        mShimmer.hideShimmer();
                        mShimmer.setVisibility(View.GONE);
                    }
                };


        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void firebaseSearch(String searchText) {
        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        final String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


        Query firebaseSearchQuery = reference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerAdapter<Insumo, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Insumo, ViewHolder>(
                        Insumo.class,
                        R.layout.card_insumo,
                        ViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Insumo insumo, int i) {

                        final Object TAG = getRef(i).getKey();
                        final String insumoID = getRef(i).getKey();

                        viewHolder.setDetails(getApplicationContext(), insumoID, insumo.getName(), insumo.getImage(), insumo.getDescription(), insumo.getQuantity(), insumo.getOwner_photo());
                        viewHolder.setActions(getApplicationContext(), TAG, insumoID, user, insumo.getOwner(), photo, name, insumo.getName());

                    }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings3:
                cerrarSesion();
                return true;
            default:
                return true;
        }
    }

    private void cerrarSesion() {
        destruirTokenFirebase();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void handleSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) { // Si el user tiene una session activa
            textBienvenida = findViewById(R.id.texto_bienvenida);
            textBienvenida.setText("¡Bienvenido " + user.getDisplayName() + "!");
        }
    }

    public void guardarDatosUsuarioFirebase() {
        SharedPreferences preferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE);
        final String retrivedToken = preferences.getString("TOKEN", null);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Usuarios");

        Query usuarioQuery = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("id").equalTo(user.getUid());

        usuarioQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) { // Si existe, actualizo el token

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        db.child(ds.getKey()).child("token").setValue(retrivedToken);
                    }

                } else { // Si no existe, me guardo el objeto en firebase

                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("id", user.getUid());
                    usuario.put("name", user.getDisplayName());
                    usuario.put("email", user.getEmail());
                    usuario.put("photo", user.getPhotoUrl().toString());
                    usuario.put("token", retrivedToken);
                    db.push().setValue(usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MenuActivity", "Hubo un error");
            }
        });
    }

    public void destruirTokenFirebase() {
        String uuid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query usuarioQuery = db.orderByChild("id").equalTo(uuid);

        usuarioQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    db.child(ds.getKey()).child("token").setValue("null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void mostrarAnimacionLoading(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        loading.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator loading) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                loading.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

}

