package com.example.weheal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    private EditText Usuario, Email, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Usuario = (EditText) findViewById(R.id.input_usuario);
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

    public void validarRegistro(View v){
        validarEditText(Usuario, "Usuario");
        validarEditText(Email, "Email");
        validarEditText(Password, "Password");
    }

    public void validarEditText(EditText element, String nombre){
        if(element.getText().toString().isEmpty()){
            element.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            element.setHintTextColor(Color.RED);
            element.setError(nombre + " incompleto");
        } else {
            if ((element.getText().toString().length() < 6)) {
                element.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                element.setHintTextColor(Color.RED);
                element.setError(nombre + " debe ser mayor a 6 caracteres");
            } else {
                element.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                element.setHintTextColor(Color.BLACK);
            }
        }
    }
}
