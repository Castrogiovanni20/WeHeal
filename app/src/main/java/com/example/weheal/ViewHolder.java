package com.example.weheal;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setDetails(Context context, String title, String image, int quantity){

        TextView cardTitle    = view.findViewById(R.id.rTitleView);
        TextView cardQuantity = view.findViewById(R.id.rQuantityView);
        ImageView mImgView    = view.findViewById(R.id.rImageView);

        cardTitle.setText(title);
        cardQuantity.setText("Cantidad: " + quantity);
        Picasso.get().load(image).into(mImgView);
    }
}
