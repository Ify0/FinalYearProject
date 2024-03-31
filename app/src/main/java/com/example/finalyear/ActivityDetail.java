package com.example.finalyear;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyear.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class ActivityDetail extends AppCompatActivity {
    private SkincareProduct skincareProduct;
    private String imageUrl = "";
    private @NonNull ActivityDetailBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser =   FirebaseAuth.getInstance().getCurrentUser();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        skincareProduct = new SkincareProduct();
        skincareProduct.setProduct_name(getIntent().getStringExtra("Product Name"));
        skincareProduct.setBrand(getIntent().getStringExtra("Brand"));
        skincareProduct.setIngredients(getIntent().getStringExtra("Ingredients"));
        List<String> parsedExtra = Arrays.asList(getIntent().getStringArrayExtra("Parsed Extra"));
        skincareProduct.set__parsed_extra(parsedExtra);

        if (skincareProduct != null) {
            binding.detailDesc.setText(skincareProduct.getIngredients());
            binding.detailTitle.setText(skincareProduct.getProduct_name());
            binding.detailPriority.setText(skincareProduct.getBrand());
            binding.detailParsedExtra.setText(skincareProduct.get__parsed_extra().toString());
        }

        // Add an OnCheckedChangeListener to the CheckBox
        binding.favourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addToFavorites(skincareProduct);
            } else {
                removeFromFavorites(skincareProduct);
            }
        });
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
                        Toast.makeText(ActivityDetail.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(ActivityDetail.this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromFavorites(SkincareProduct product) {
        // Remove item from Firestore database
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("favourites")
                .document(product.getProduct_name())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully removed from favorites
                        Toast.makeText(ActivityDetail.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(ActivityDetail.this, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

