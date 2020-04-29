package com.example.weheal;

import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    private EditText Email, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);

        Button principal = findViewById(R.id.paginaPrincipal);

        principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

    }

    public void validarLogin(View v){
        validarEditText(Email, "Email");
        validarEditText(Password, "Password");
    }

    public void validarEditText(EditText element, String nombre){
        if(element.getText().toString().isEmpty()){
            element.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            element.setHintTextColor(Color.RED);
            element.setError(nombre + " incompleto");
        } else {
            element.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            element.setHintTextColor(Color.BLACK);
        }
    }
}


