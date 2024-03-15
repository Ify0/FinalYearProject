package com.example.finalyear;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteViewHolder extends RecyclerView.ViewHolder {
    public ImageView Image;
    public TextView productName;
    public TextView BrandName;
    public CardView Card;
    public CheckBox favoriteCheckBox;

    public FavouriteViewHolder(View itemView) {
        super(itemView);
        Image = itemView.findViewById(R.id.Image);
        productName = itemView.findViewById(R.id.productName);
        BrandName = itemView.findViewById(R.id.BrandName);
        Card = itemView.findViewById(R.id.Card);
        favoriteCheckBox = itemView.findViewById(R.id.favouritescheckBox);
    }
}
