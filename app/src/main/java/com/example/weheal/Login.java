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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
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
    private Button Login;
    private TextView Registro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Iniciar sesión");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_login);

        Email    = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);

        Login    = (Button)   findViewById(R.id.boton_iniciar_sesion);
        Registro = (TextView) findViewById(R.id.boton_crear_cuenta);

        Login.setEnabled(false);
        Login.setBackgroundColor(Color.parseColor("#ab81ea"));

        Email.addTextChangedListener(loginTextWatcher);
        Password.addTextChangedListener(loginTextWatcher);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean formularioValido = validarLogin((view));
                if (formularioValido == true) {
                    signIn(Email.getText().toString(), Password.getText().toString());
                }
            }
        });

        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registro.class);
                startActivity(intent);
            }
        });
    }


    // Login utilizando credenciales
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            mostrarDialogo("Login exitoso", "Bienvenido " + user.getEmail() );
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

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String emailInput = Email.getText().toString().trim();
            String passwordInput = Password.getText().toString().trim();

            if(!emailInput.isEmpty() && !passwordInput.isEmpty()){
                Login.setBackgroundColor(Color.parseColor("#6200EE"));
                Login.setEnabled(true);
            } else {
                Login.setBackgroundColor(Color.parseColor("#ab81ea"));
                Login.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}


