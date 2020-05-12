package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity {

    Button cargaMedicacion;
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}

