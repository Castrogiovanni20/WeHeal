package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * @description Clase obsoleta. Registro deshabilitado 24/06/2020
 */
public class Registro extends AppCompatActivity {

    private static final String TAG = "Registro";
    private EditText Usuario, Email, Password;
    private Button Registro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Crear cuenta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_registro);

        Usuario  = (EditText) findViewById(R.id.input_usuario);
        Email    = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);

        Registro  = (Button) findViewById(R.id.boton_registrarse);

        Registro.setEnabled(false);
        Registro.setBackgroundColor(Color.parseColor("#ab81ea"));

        Usuario.addTextChangedListener(registroTextWatcher);
        Email.addTextChangedListener(registroTextWatcher);
        Password.addTextChangedListener(registroTextWatcher);

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
                        }else {
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

    private TextWatcher registroTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String usuarioInput = Usuario.getText().toString().trim();
            String emailInput = Email.getText().toString().trim();
            String passwordInput = Password.getText().toString().trim();

            if(!usuarioInput.isEmpty() && !emailInput.isEmpty() && !passwordInput.isEmpty()){
                Registro.setBackgroundColor(Color.parseColor("#6200EE"));
                Registro.setEnabled(true);
            } else {
                Registro.setBackgroundColor(Color.parseColor("#ab81ea"));
                Registro.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
