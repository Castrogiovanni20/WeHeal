package com.example.weheal;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class ServiceFirebaseMessaging extends FirebaseMessagingService  {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("MessagingFirebase", "Mi token es:" + s);
        guardarToken(s);
    }

    public void guardarToken(String s){
        super.onNewToken(s);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Tokens");
        ref.child("token").setValue(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Log.e("MessagingFirebase", "Mensaje enviado por " + from);

        if(remoteMessage.getData().size() > 0){ // Notificacion personalizada Firebase

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            checkCompatibilidad(title, body);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkCompatibilidad(String title, String body){
        String id = "mensaje";

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){ // Las notificaciones van a ser compatibles unicamentes en versiones mayores a Oreo
            NotificationChannel nc = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true); // Para mostrar el icono de la notificacion
            nm.createNotificationChannel(nc);
        }

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(clickNoti()) // Cuando hacemos click en la notificacion, hacemos un intent a nuestro mainActivity
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentInfo("New");

        Random random = new Random();
        int idNotify = random.nextInt(1000);
        nm.notify(idNotify, builder.build());
    }

    public PendingIntent clickNoti(){
        Intent nf = new Intent(getApplicationContext(), MainActivity.class);
        nf.putExtra("colour", "rojo");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, nf,0);
    }

}
