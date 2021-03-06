package com.cwb.weheal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CargarMedicacion extends AppCompatActivity {

    private static final String TAG = "CargarMedicacion";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_INTENT = 0;
    private Insumo insumo;
    private TextInputLayout nombre, cantidad, descripcion;
    private EditText nombreInsumo, descripcionInsumo, cantidadInsumo;
    private TextView subirFoto;
    private Spinner tipoInsumo;
    private Button cargarInsumo, sacarFoto;
    private AHBottomNavigation bottomNavigation;
    private ClipData.Item cerrarSesion;
    private LottieAnimationView loading;
    private DatabaseReference db;
    private FirebaseFirestore mStorage;
    private Uri uriFile = null;
    private String storageURI;

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


        bottomNavigation = findViewById(R.id.bottom_navigation);
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Add", R.drawable.ic_add_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications_none_black_24dp);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#ff8d96"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setCurrentItem(1);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        return true;
                    case 1:
                        return true;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), Notificaciones.class));
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
                boolean formularioValido = validarFormulario(view);
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
    protected void onStart() {
        super.onStart();
        setearBadgeNotificaciones();
    }


    /**
     * @description Se ejecuta como resultado de sacar una foto o seleccionar una foto de la gallery
     * @param requestCode GALLERY_INTENT
     * @param resultCode RESULT_OK en caso que este ok
     * @param data Contiene la URI
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            uriFile = data.getData();
            Toast.makeText(this, "Imagen cargada exitosamente", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            Toast.makeText(this, "Foto cargada exitosamente", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @description Almacena la imagen del storage y persiste los datos en Firebase
     * @param nombre Nombre del insumo
     * @param tipo Tipo de insumo
     * @param description Descripcion del insumo
     * @param cantidad Cantidad del insumo
     */
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

    /**
     * @description Persistir los datos en Firebase
     * @param nombre Nombre del insumo
     * @param tipo  Tipo de insumo
     * @param description Descripcion del insumo
     * @param cantidad Cantidad del insumo
     */
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


    /**
     * @description Mostrar animacion de loading cuando se sube el insumo
     */
    private void mostrarAnimacionLoading(){
        nombre.setVisibility(View.INVISIBLE);
        cantidad.setVisibility(View.INVISIBLE);
        descripcion.setVisibility(View.INVISIBLE);

        tipoInsumo.setVisibility(View.INVISIBLE);
        cargarInsumo.setVisibility(View.INVISIBLE);
        subirFoto.setVisibility(View.INVISIBLE);
        sacarFoto.setVisibility(View.INVISIBLE);

        loading.setVisibility(View.VISIBLE);
    }


    /**
     * @description Validar el formulario de carga de medicacion
     * @return
     */
    public boolean validarFormulario(View view){
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


        if (uriFile == null){
            Snackbar snackbar = Snackbar.make(view, "Debes cargar una foto del insumo",Snackbar.LENGTH_LONG);
            snackbar.setDuration(5000);
            snackbar.show();
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Snackbar", "Mensaje cerrado");
                }
            });
            errores++;
        }

        if (errores > 0 ){
            formularioValido = false;
        }

        return formularioValido;
    }


    /**
     * @description Intent a camara
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
                Log.d(TAG, "ImageFile: " + photoFile.toString());
            } catch (IOException ex) {
                Log.d(TAG, "Ocurrio un error");
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.cwb.weheal.fileprovider", photoFile);
                uriFile = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    /**
     * @description Genera una fie con formato .jpg
     * @return Retorna un file
     * @throws IOException
     */
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
        return image;
    }


    /**
     * @description Setear en el badge la cantidad de notificaciones.
     * Realiza una query para consultar por la cantidad de notificaciones.
     *
     */
    public void setearBadgeNotificaciones(){
        final String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference refNotifications = FirebaseDatabase.getInstance().getReference("Notificaciones");
        final Query firebaseQuery = refNotifications.orderByChild("destination").equalTo(idUser);
        firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    count++;
                }
                actualizarBadgeNotificaciones(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * @description Actualizar el badge con la cantidad de notificaciones
     * @param cant La cantidad de notificaciones a setear en el badge
     */
    public void actualizarBadgeNotificaciones(int cant){
        if (cant != 0){
            AHNotification notification = new AHNotification.Builder()
                    .setText(String.valueOf(cant))
                    .setBackgroundColor(ContextCompat.getColor(CargarMedicacion.this, R.color.background_notification))
                    .setTextColor(ContextCompat.getColor(CargarMedicacion.this, R.color.text_notification))
                    .build();
            bottomNavigation.setNotification(notification, 2);
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText("")
                    .setBackgroundColor(ContextCompat.getColor(CargarMedicacion.this, R.color.text_notification))
                    .setTextColor(ContextCompat.getColor(CargarMedicacion.this, R.color.text_notification))
                    .build();
            bottomNavigation.setNotification(notification, 2);
        }
    }
}
