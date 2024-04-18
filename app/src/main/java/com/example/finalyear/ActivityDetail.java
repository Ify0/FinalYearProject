package com.example.finalyear;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

        // Set up RecyclerView for parsed extra
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.detailParsedExtra.setLayoutManager(layoutManager);
        ParsedExtraAdapter adapter = new ParsedExtraAdapter(skincareProduct.get__parsed_extra());
        binding.detailParsedExtra.setAdapter(adapter);

        if (skincareProduct != null) {
            binding.detailDesc.setText(skincareProduct.getIngredients());
            binding.detailTitle.setText(skincareProduct.getProduct_name());
            binding.detailPriority.setText(skincareProduct.getBrand());
           // binding.detailParsedExtra.setText(skincareProduct.get__parsed_extra().toString());
            StringBuilder parsedExtraBuilder = new StringBuilder();
            for (String extra : skincareProduct.get__parsed_extra()) {
                parsedExtraBuilder.append(extra);
            }
        }


        // Add an OnCheckedChangeListener to the CheckBox
        binding.favourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addToFavorites(skincareProduct);
            } else {
                removeFromFavorites(skincareProduct);
            }
        });

        // Set OnClickListener for the info button
        binding.infoButton.setOnClickListener(v -> {
            showColorSchemeDialog();
        });
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click event
                onBackPressed();
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

    private void showColorSchemeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
        builder.setTitle("Color Scheme for Ingredients Awareness");
        builder.setMessage("Yellow indicates a low penalty for your skin, orange indicates a medium penalty, and red indicates a strong penalty for your skin. Make informed decisions based on this color scheme.");
        builder.setPositiveButton("Got it", (dialog, which) -> {
            // Do nothing, just close the dialog
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

