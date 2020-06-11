package com.example.weheal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.animation.Animator;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class CargarMedicacion extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private Insumo insumo;
    private TextInputLayout nombre, cantidad, descripcion;
    private EditText nombreInsumo, descripcionInsumo, cantidadInsumo;
    private TextView subirFoto;
    private Spinner tipoInsumo;
    private Button cargarInsumo, sacarFoto;
    private BottomNavigationView nav;
    private ClipData.Item cerrarSesion;
    private LottieAnimationView loading;
    private DatabaseReference db;
    private FirebaseFirestore mStorage;
    private Uri uriFile = null;
    private String storageURI;
    private static final int GALLERY_INTENT = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final int REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_medicacion);

        getSupportActionBar().setTitle("Cargar insumo");

        db = FirebaseDatabase.getInstance().getReference().child("Insumos");
        mStorage = FirebaseFirestore.getInstance();

        nombreInsumo       = (EditText) findViewById(R.id.input_nombreInsumo);
        descripcionInsumo  = (EditText) findViewById(R.id.input_description);
        subirFoto          = (TextView) findViewById(R.id.subirFoto);
        tipoInsumo         = (Spinner) findViewById(R.id.insumosSpinner);
        cantidadInsumo     = (EditText) findViewById(R.id.cantidadDonarEditText);

        nombre      = (TextInputLayout) findViewById(R.id.nombreInsumo);
        cantidad    = (TextInputLayout) findViewById(R.id.cantInsumo);
        descripcion = (TextInputLayout) findViewById(R.id.descripcion);

        sacarFoto    = (Button) findViewById(R.id.sacarFoto);
        cargarInsumo = (Button) findViewById(R.id.cargarInsumoButton);

        loading = (LottieAnimationView) findViewById(R.id.loading);

        nav = findViewById(R.id.bottom_navigation);
        nav.setBackgroundColor(Color.parseColor("#ffffff"));
        nav.setSelectedItemId(R.id.nav_addMedicacion);

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        return true;
                    case R.id.nav_notifications:
                        startActivity(new Intent(getApplicationContext(), Notificaciones.class));
                        return true;
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

        sacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        cargarInsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean formularioValido = validarFormulario();
                if(formularioValido == true){
                    int cantidad = Integer.parseInt(cantidadInsumo.getText().toString());
                    insertMedicamento(nombreInsumo.getText().toString(), tipoInsumo.getSelectedItem().toString(), descripcionInsumo.getText().toString(), cantidad);
                    /*Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                    startActivity(intent); */
                    mostrarAnimacionLoading();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            if(data.getData()!= null){
                uriFile = data.getData();
            }
            Toast.makeText(this, "Imagen cargada con exito", Toast.LENGTH_SHORT).show();
        }
    }

    protected void insertMedicamento(final String nombre, final String tipo, final String description, final int cantidad){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference filePath = storageRef.child("fotos").child(uriFile.getLastPathSegment());

        filePath.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storageURI = uri.toString();
                        persistirDatos(nombre, tipo, description, cantidad);
                    }
                });
            }
        });

    }

    public void persistirDatos(String nombre, String tipo, String description, int cantidad){
        final String id_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String owner_photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

        insumo = new Insumo();
        insumo.setName(nombre);
        insumo.setType(tipo);
        insumo.setDescription(description);
        insumo.setQuantity(cantidad);
        insumo.setImage(storageURI);
        insumo.setOwner(id_user);
        insumo.setOwner_photo(owner_photo);

        db.push().setValue(insumo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), AnimationActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CargarMedicacion.this, "Error al cargar el medicamento", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarAnimacionLoading(){
        nombre.setVisibility(View.INVISIBLE);
        cantidad.setVisibility(View.INVISIBLE);
        descripcion.setVisibility(View.INVISIBLE);

        tipoInsumo.setVisibility(View.INVISIBLE);
        cargarInsumo.setVisibility(View.INVISIBLE);
        subirFoto.setVisibility(View.INVISIBLE);

        loading.setVisibility(View.VISIBLE);

    }

    public boolean validarFormulario(){
        int errores = 0;
        boolean formularioValido = true;

        if(nombreInsumo.getText().toString().isEmpty()){
            nombreInsumo.setHintTextColor(Color.RED);
            nombreInsumo.setError("Nombre del insumo incompleto");
            errores++;
        } else {
            if ((nombreInsumo.getText().toString().length() < 6)) {
                nombreInsumo.setHintTextColor(Color.RED);
                nombreInsumo.setError("El nombre del insumo debe ser mayor a 6 caracteres");
                errores++;
            } else {
                nombreInsumo.setHintTextColor(Color.BLACK);
            }
        }

        if(cantidadInsumo.getText().toString().isEmpty()){
            cantidadInsumo.setHintTextColor(Color.RED);
            cantidadInsumo.setError("Cantidad de insumo incompleto");
            errores++;
        } else {
            cantidadInsumo.setHintTextColor(Color.BLACK);
        }


        if (errores > 0 ){
            formularioValido = false;
        }

        return formularioValido;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
                Log.d("CargarMedicacion", "Test: " + photoFile.toString());
            } catch (IOException ex) {
                Log.d("CargarMedicacion", "Ocurrio un error");
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("CargarMedicacion", "Entro en el if");
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.weheal.fileprovider", photoFile);
                uriFile = photoURI;
                Log.d("CargarMedicacion", "Urifile: " + uriFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } else {
                Log.d("CargarMedicacion", "No entro en el if");
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String currentPhotoPath = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("CargarMedicacion", "El path es: " + currentPhotoPath);

        return image;
    }
}
