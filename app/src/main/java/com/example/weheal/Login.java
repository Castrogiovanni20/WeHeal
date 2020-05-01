package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText Email, Password;
    private Button Principal, Login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        Email    = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);

        Principal = findViewById(R.id.paginaPrincipal);
        Login     = findViewById(R.id.boton_iniciar_sesion);

        Principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean formularioValido = validarLogin((view));
                if (formularioValido == true) {
                    signIn(Email.getText().toString(), Password.getText().toString());
                }
            }
        });
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            mostrarDialogo("Login exitoso", "Bienvenido");
                        } else {
                            mostrarDialogo("Usuario y/o contraseña incorrecto.", "Error en el login");
                        }
                    }
                });
    }

    public boolean validarLogin(View v){
        boolean formularioValido = false;
        boolean emailValido        = validarEditText(Email, "Email");
        boolean passwordValida     = validarEditText(Password, "Password");
        boolean formatoEmailValido = validarEmail(Email.getText().toString());

        if(emailValido == true && passwordValida == true && formatoEmailValido == true){
            formularioValido = true;
        }
        return formularioValido;
    }

    public boolean validarEditText(EditText element, String nombre){
        if(element.getText().toString().isEmpty()){
            element.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            element.setHintTextColor(Color.RED);
            element.setError(nombre + " incompleto");
            return false;
        } else {
            if ((element.getText().toString().length() < 6)) {
                element.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                element.setHintTextColor(Color.RED);
                element.setError(nombre + " debe ser mayor a 6 caracteres");
                return false;
            } else {
                element.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                element.setHintTextColor(Color.BLACK);
                return true;
            }
        }
    }

    public boolean validarEmail(String email){
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Email.setHintTextColor(Color.RED);
            Email.setError("Email invalido");
            return false;
        } else {
            return true;
        }
    }

    private void mostrarDialogo(String msg, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Mensaje", "Accion cancelada");
                    }
                })
                .show();
    }
}


