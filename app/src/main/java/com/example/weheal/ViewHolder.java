package com.example.weheal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolder extends RecyclerView.ViewHolder {

    private DatabaseReference db;
    private View view;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;

    }

    public void setDetails(Context context, String ID_insumo, String title, String image, String description, int quantity, String owner_photo){


        TextView cardTitle       = view.findViewById(R.id.rTitleView);
        TextView cardQuantity    = view.findViewById(R.id.rQuantityView);
        TextView cardDescription = view.findViewById(R.id.rDescription);
        ImageView mImgView       = view.findViewById(R.id.rImageView);
        CircleImageView mImageProfile = view.findViewById(R.id.profile_image);
        Button mButton = view.findViewById(R.id.rButton);

        cardTitle.setText(title);
        cardQuantity.setText("Cantidad: " + quantity);
        cardDescription.setText(description);
        mButton.setTag(ID_insumo);
        Picasso.get().load(image).into(mImgView);
        Picasso.get().load(owner_photo).into(mImageProfile);
    }

    public void setActions(final Object TAG, final String insumoID, final String userID, final String ownerID, final String photo_postulant, final String name_postulant){
        final Button btn = view.findViewWithTag(TAG);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarInsumo(insumoID, userID, ownerID, photo_postulant, name_postulant);
                showSnackbar();
            }
        });
    }


    public void solicitarInsumo(String insumoID, String userID, String ownerID, String photo_postulant, String name_postulant){
        db = FirebaseDatabase.getInstance().getReference().child("Notificaciones");
        final String state = "Waiting";

        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("destination",ownerID);
        solicitud.put("state", state);
        solicitud.put("id_medical_input",insumoID);
        solicitud.put("postulant", userID);
        solicitud.put("photo_postulant", photo_postulant);
        solicitud.put("name_postulant", name_postulant);

        db.push().setValue(solicitud)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ViewHolder", "Insumo solicitado con exito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ViewHolder", "Ocurrio un error al solicitar un insumo");
                    }
                });
    }


    public void showSnackbar(){
        Snackbar snackbar = Snackbar.make(view, "Insumo solicitado con exito", Snackbar.LENGTH_LONG);
        snackbar.setDuration(5000);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Snackbar", "Test snack");
            }
        });
        snackbar.show();
    }
}
