package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class CargarMedicacion extends AppCompatActivity {

    private EditText nombreInsumo, cantidadInsumo;
    private Spinner tipoInsumo;
    private Button cargarInsumo, subirFoto;
    BottomNavigationView nav;
    private FirebaseFirestore db;
    private FirebaseFirestore mStorage;
    private Uri uriFile = null;
    private static final int GALLERY_INTENT = 1;

    private final int REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_medicacion);

        getSupportActionBar().setTitle("Cargar insumo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseFirestore.getInstance();

        nombreInsumo       = (EditText) findViewById(R.id.input_nombreInsumo);
        tipoInsumo         = (Spinner) findViewById(R.id.insumosSpinner);
        cantidadInsumo     = (EditText) findViewById(R.id.cantidadDonarEditText);

        subirFoto    = (Button) findViewById(R.id.subirFotoButton);
        cargarInsumo = (Button) findViewById(R.id.cargarInsumoButton);

        nav = findViewById(R.id.bottom_navigation);
        nav.setBackgroundColor(Color.parseColor("#ffffff"));
        nav.setSelectedItemId(R.id.nav_addMedicacion);

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                    case R.id.nav_addMedicacion:
                        return true;
                }
                return false;
            }
        });

        subirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        cargarInsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean formularioValido = validarFormulario();
                if(formularioValido == true){
                    int cantidad = Integer.parseInt(cantidadInsumo.getText().toString());
                    insertMedicamento(nombreInsumo.getText().toString(), tipoInsumo.getSelectedItem().toString(), cantidad);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            uriFile = data.getData();
            Toast.makeText(this, "Imagen cargada con exito", Toast.LENGTH_SHORT).show();
        }
    }

    protected void storageFile(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference filePath = storageRef.child("fotos").child(uriFile.getLastPathSegment());

        filePath.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CargarMedicacion.this, "Se subio exitosamente la foto", Toast.LENGTH_SHORT).show();
                }
        });
    }

    public void insertMedicamento(String nombre, String tipo, int cantidad){
        // Create a new user with a first and last name
        Map<String, Object> medicamentos = new HashMap<>();
        medicamentos.put("nombre", nombre);
        medicamentos.put("tipo", tipo);
        medicamentos.put("cantidad", cantidad);

        if (uriFile != null){
            storageFile();
        }

        db.collection("medicamentos")
                .add(medicamentos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CargarMedicacion.this, "Medicamento cargado con exito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CargarMedicacion.this, "Error al cargar el edicamento", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean validarFormulario(){
        int errores = 0;
        boolean formularioValido = true;

        if(nombreInsumo.getText().toString().isEmpty()){
            nombreInsumo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            nombreInsumo.setHintTextColor(Color.RED);
            nombreInsumo.setError("Nombre del insumo incompleto");
            errores++;
        } else {
            if ((nombreInsumo.getText().toString().length() < 6)) {
                nombreInsumo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                nombreInsumo.setHintTextColor(Color.RED);
                nombreInsumo.setError("El nombre del insumo debe ser mayor a 6 caracteres");
                errores++;
            } else {
                nombreInsumo.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                nombreInsumo.setHintTextColor(Color.BLACK);
            }
        }

        if(cantidadInsumo.getText().toString().isEmpty()){
            cantidadInsumo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            cantidadInsumo.setHintTextColor(Color.RED);
            cantidadInsumo.setError("Cantidad de insumo incompleto");
            errores++;
        } else {
            cantidadInsumo.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            cantidadInsumo.setHintTextColor(Color.BLACK);
        }


        if (errores > 0 ){
            formularioValido = false;
        }

        return formularioValido;
    }
    

}
