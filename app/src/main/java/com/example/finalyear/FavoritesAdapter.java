package com.example.finalyear;

// FavoritesAdapter.java
import android.app.Activity;
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
        //SkincareProduct product = favoritesList.get(position);
        holder.productName.setText(favoritesList.get(position).getProduct_name());
        holder.BrandName.setText(favoritesList.get(position).getBrand());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Add more views and data binding as needed
        holder.Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ActivityDetail.class);
                    intent.putExtra("Product Name", favoritesList.get(pos).getProduct_name());
                    intent.putExtra("Brand", favoritesList.get(pos).getBrand());
                    intent.putExtra("Ingredients", favoritesList.get(pos).getIngredients());
                    // Convert List<String> to String array
                    String[] parsedExtraArray = favoritesList.get(pos).get__parsed_extra().toArray(new String[0]);
                    intent.putExtra("Parsed Extra", parsedExtraArray);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void searchDataList(List<SkincareProduct> searchList) {
        favoritesList = searchList;
        notifyDataSetChanged();
    }

    private void addToFavorites(SkincareProduct product) {
        // Create a new document in the "favourites" collection under the user's UID
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("favourites")
                .document(product.getProduct_name()) // Assuming there is a unique identifier for each product
                .set(product) // You can customize this based on your data structure
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully added to favorites
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(context, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromFavorites(SkincareProduct product) {
        // Get a reference to the document in the "favourites" collection
        // Replace "currentUser.getUid()" with the actual method or variable you use to get the current user's UID
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("favourites")
                .document(product.getProduct_name()) // Assuming there is a unique identifier for each product
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName;
        // Add more views as needed

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.recTitle);
            // Initialize more views as needed
        }

    }
}

