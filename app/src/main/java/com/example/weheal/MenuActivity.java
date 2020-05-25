package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MenuActivity extends AppCompatActivity {

    Button cargaMedicacion;
    ImageButton search;
    EditText searchField;
    TextView textBienvenida;
    BottomNavigationView nav;
    RecyclerView mRecyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        handleSession();

        search      = findViewById(R.id.search);
        searchField = findViewById(R.id.search_field);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Insumos");


        nav = findViewById(R.id.bottom_navigation);
        nav.setBackgroundColor(Color.parseColor("#ffffff"));
        nav.setSelectedItemId(R.id.nav_home);

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_addMedicacion:
                        startActivity(new Intent(getApplicationContext(), CargarMedicacion.class));
                        return true;
                }
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseSearch(searchField.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Insumo, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Insumo, ViewHolder>(
                        Insumo.class,
                        R.layout.image,
                        ViewHolder.class,
                        reference
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Insumo insumo, int i) {

                        viewHolder.setDetails(getApplicationContext(), insumo.getName(), insumo.getImage(), insumo.getDescription(), insumo.getQuantity());

                    }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void firebaseSearch(String searchText){
        Query firebaseSearchQuery = reference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Insumo, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Insumo, ViewHolder>(
                        Insumo.class,
                        R.layout.image,
                        ViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Insumo insumo, int i) {

                        viewHolder.setDetails(getApplicationContext(), insumo.getName(), insumo.getImage(), insumo.getDescription(), insumo.getQuantity());

                    }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public boolean onCreateOptionsMenu(Menu menu){
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
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void handleSession(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) { // Si el user tiene una session activa
            textBienvenida = findViewById(R.id.texto_bienvenida);
            textBienvenida.setText("¡Bienvenido " + user.getDisplayName() + "!");
        }
    }


}

