package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.util.Log;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registro extends AppCompatActivity {

    private static final String TAG = "Registro";
    private EditText Usuario, Email, Password;
    private Button Principal, Registro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_registro);

        Usuario  = (EditText) findViewById(R.id.input_usuario);
        Email    = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);

        Principal = findViewById(R.id.paginaPrincipal);
        Registro  = findViewById(R.id.registrarse);

        Principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Registro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                boolean formularioValido = validarRegistro((view));
                if (formularioValido == true){
                    createAccount(Email.getText().toString(), Password.getText().toString());
                }
            }
        });

    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            mostrarDialogo("Usuario registrado con exito.", "Registro exitoso");

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mostrarDialogo("Ocurrio un error en el registro, por favor intente nuevamente.", "Fallo en el registro");
                        }
                    }
                });

    }

    public boolean validarRegistro(View v){
        boolean formularioValido   = false;
        boolean usuarioValido      = validarEditText(Usuario, "Usuario");
        boolean emailValido        = validarEditText(Email, "Email");
        boolean passwordValida     = validarEditText(Password, "Password");
        boolean formatoEmailValido = validarEmail(Email.getText().toString());

        if (emailValido == true && passwordValida == true && formatoEmailValido == true && usuarioValido == true){
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
