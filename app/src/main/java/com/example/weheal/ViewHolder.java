package com.example.weheal;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {
    View view;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;

    }

    public void setDetails(Context context, String title, String image, String description, int quantity){


        TextView cardTitle    = view.findViewById(R.id.rTitleView);
        TextView cardQuantity = view.findViewById(R.id.rQuantityView);
        TextView cardDescription = view.findViewById(R.id.rDescription);
        ImageView mImgView    = view.findViewById(R.id.rImageView);

        cardTitle.setText(title);
        cardQuantity.setText("Cantidad: " + quantity);
        cardDescription.setText(description);

        Picasso.get().load(image).into(mImgView);
    }


}
