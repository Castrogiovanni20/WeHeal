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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void setActions(final Context context, final Object TAG, final String insumoID, final String userID, final String ownerID, final String photo_postulant, final String name_postulant, final String insumoName){
        final Button btn = view.findViewWithTag(TAG);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarInsumo(insumoID, userID, ownerID, photo_postulant, name_postulant);
                showSnackbar();
                enviarPush(context, ownerID, name_postulant, insumoName);
            }
        });
    }


    public void solicitarInsumo(String insumoID, String userID, String ownerID, String photo, String name_postulant){
        db = FirebaseDatabase.getInstance().getReference().child("Notificaciones");
        final String STATE = "Waiting";

        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("destination",ownerID);
        solicitud.put("state", STATE);
        solicitud.put("id_medical_input",insumoID);
        solicitud.put("postulant", userID);
        solicitud.put("photo", photo);
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

    public void enviarPush(Context context, String idDestino, final String nombrePostulante, final String nombreInsumo){
        final RequestQueue myRequest = Volley.newRequestQueue(context);
        final JSONObject json = new JSONObject();

        Query tokenDestinoQuery  = FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("id").equalTo(idDestino);
        tokenDestinoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String tokenDestino = ds.child("token").getValue().toString();

                    try {
                        json.put("to", tokenDestino);
                        JSONObject notificacion = new JSONObject();

                        notificacion.put("title", "Solicitud de insumo");
                        notificacion.put("body",  nombrePostulante + " solicito la donación de " + nombreInsumo);

                        json.put("data", notificacion);
                        String URL = "https://fcm.googleapis.com/fcm/send";

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, json, null, null){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization","key=AAAAG3ZP9Yw:APA91bF_IJd3q8QjWvnPpP1tLl_pv9aal4KrXloZu87FS_xkpSJgA6gxvBsPq6Sjq_IN5Ro2pmAhMj6_IeHn-R5HIhoJkStJEDK8KPdX9l8M7yyJcCquNHe1VtotFnKWToDJbLt_uBAH");
                                return header;
                            }
                        };

                        myRequest.add(request);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
