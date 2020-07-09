package com.cwb.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.facebook.login.LoginManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    ImageButton search;
    EditText searchField;
    TextView textBienvenida;
    AHBottomNavigation bottomNavigation;
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
        bottomNavigation.setAccentColor(Color.parseColor("#ff8d96"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        return true;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), CargarMedicacion.class));
                        return true;
                    case 2:
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

                        final Object TAG  = getRef(i).getKey();
                        final String insumoID = getRef(i).getKey();

                        viewHolder.setDetails(getApplicationContext(), insumoID, insumo.getName(), insumo.getImage(), insumo.getDescription(), insumo.getQuantity(), insumo.getOwner_photo());
                        viewHolder.setActions(getApplicationContext(), TAG, insumoID, user, insumo.getOwner(), photo, name, insumo.getName());

                        mShimmer.stopShimmer();
                        mShimmer.hideShimmer();
                        mShimmer.setVisibility(View.GONE);
                    }
                };


        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        setearBadgeNotificaciones();
    }


    /**
     * @description Buscar un insumo en la DB de Firebase y rellenar el resultado en el RecyclerView
     * @param searchText
     */
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


    /**
     * @description Menu con la opcion de cerrar session
     * @param item
     * @return
     */
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


    /**
     * @description Cierra la session, destruye el token del usuario, y hace un Intent a MainActivity
     */
    private void cerrarSesion() {
        destruirTokenFirebase();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    /**
     * @description Mostrar mensaje de bienvenida
     */
    private void handleSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) { // Si el user tiene una session activa
            textBienvenida = findViewById(R.id.texto_bienvenida);
            textBienvenida.setText("¡Bienvenido " + user.getDisplayName() + "!");
        }
    }


    /**
     * @description Guardar los datos del usuario, incluido el token
     */
    public void guardarDatosUsuarioFirebase() {
        SharedPreferences preferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE);
        final String retrivedToken = preferences.getString("TOKEN", null);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Usuarios");

        Query usuarioQuery = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("email").equalTo(user.getEmail());

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


    /**
     * @description Destruir el token de Firebase
     */
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


    /**
     * @description Mostrar animacion de Loading
     */
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


    /**
     * @description Setear en el badge la cantidad de notificaciones.
     * Realiza una query para consultar por la cantidad de notificaciones.
     *
     */
    public void setearBadgeNotificaciones(){
        final String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference refNotifications = firebaseDatabase.getReference("Notificaciones");
        final Query firebaseQuery = refNotifications.orderByChild("destination").equalTo(idUser);
        firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    count++;
                }
                actualizarBadgeNotificaciones(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * @description Actualizar el badge con la cantidad de notificaciones
     * @param cant La cantidad de notificaciones a setear en el badge
     */
    public void actualizarBadgeNotificaciones(int cant){
        if (cant != 0){
            AHNotification notification = new AHNotification.Builder()
                    .setText(String.valueOf(cant))
                    .setBackgroundColor(ContextCompat.getColor(MenuActivity.this, R.color.background_notification))
                    .setTextColor(ContextCompat.getColor(MenuActivity.this, R.color.text_notification))
                    .build();
            bottomNavigation.setNotification(notification, 2);
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText("")
                    .setBackgroundColor(ContextCompat.getColor(MenuActivity.this, R.color.text_notification))
                    .setTextColor(ContextCompat.getColor(MenuActivity.this, R.color.text_notification))
                    .build();
            bottomNavigation.setNotification(notification, 2);
        }
    }

}

