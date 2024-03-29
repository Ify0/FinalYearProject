package com.example.finalyear;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<myViewHolder> {
    private final Context context;
    private List<SkincareProduct> dataList;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;

    public MyAdapter(Context context, List<SkincareProduct> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        holder.recTitle.setText(dataList.get(position).getProduct_name());
        holder.recDesc.setText(dataList.get(position).getBrand());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ActivityDetail.class);
                    intent.putExtra("Product Name", dataList.get(pos).getProduct_name());
                    intent.putExtra("Brand", dataList.get(pos).getBrand());
                    intent.putExtra("Ingredients", dataList.get(pos).getIngredients());
                    // Convert List<String> to String array
                    String[] parsedExtraArray = dataList.get(pos).get__parsed_extra().toArray(new String[0]);
                    intent.putExtra("Parsed Extra", parsedExtraArray);
                    context.startActivity(intent);
                }
            }
        });

        holder.favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    SkincareProduct selectedProduct = dataList.get(pos);

                    // Perform actions when the favorite status changes
                    if (isChecked) {
                        // Add the product to Firebase or your favorites list
                        // You might want to create a method to handle Firebase operations
                        addToFavorites(selectedProduct);
                    } else {
                        // Remove the product from Firebase or your favorites list
                        // You might want to create a method to handle Firebase operations
                        removeFromFavorites(selectedProduct);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(List<SkincareProduct> searchList) {
        dataList = searchList;
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
}
