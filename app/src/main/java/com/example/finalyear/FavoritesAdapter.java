package com.example.finalyear;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavouriteViewHolder> {

    private final Context context;
    private List<SkincareProduct> favoritesList;

    private FirebaseFirestore db;

    private FirebaseUser currentUser;




    public FavoritesAdapter(Context context, List<SkincareProduct> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        SkincareProduct product = favoritesList.get(position); // Define 'product' here
        holder.productName.setText(product.getProduct_name());
        holder.BrandName.setText(product.getBrand());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ActivityDetail.class);
                    intent.putExtra("Product Name", product.getProduct_name());
                    intent.putExtra("Brand", product.getBrand());
                    intent.putExtra("Ingredients", product.getIngredients());
                    // Convert List<String> to String array
                    String[] parsedExtraArray = product.get__parsed_extra().toArray(new String[0]);
                    intent.putExtra("Parsed Extra", parsedExtraArray);
                    context.startActivity(intent);
                }
            }
        });

        holder.favoriteCheckBox.setOnCheckedChangeListener(null); // Avoid triggering listener on recycled views
        holder.favoriteCheckBox.setChecked(true); // Always checked by default in favorites

        holder.favoriteCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                removeFromFavorites(product);
            }
        });
    }


    @Override
    public int getItemCount() {
        return favoritesList.size();
    }




    public void removeFromFavorites(SkincareProduct product) {
        int position = favoritesList.indexOf(product);
        if (position != -1) {
            // Remove item from favorites list
            favoritesList.remove(position);
            notifyItemRemoved(position);

            // Remove item from Firestore database
            db.collection("Users")
                    .document(currentUser.getUid())
                    .collection("favourites")
                    .document(product.getProduct_name())
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully removed from favorites
                            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle failure
                            Toast.makeText(context, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



}

