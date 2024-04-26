package com.example.finalyear;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class productInfoActivity extends AppCompatActivity {
    private SkincareProduct skincareProduct;
    private TextView productNameTv, ingredientsTv, brands;
    private RecyclerView recyclerView;

    ImageButton backButton ;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        productNameTv = findViewById(R.id.detailTitle);
        ingredientsTv = findViewById(R.id.detailDesc);
        brands = findViewById(R.id.detailPriority);
        recyclerView = findViewById(R.id.detailParsedExtra);
        backButton = findViewById(R.id.backButton);
        skincareProduct = new SkincareProduct();

        // Retrieve data from Intent
        Intent intent = getIntent();
        skincareProduct.setProduct_name(getIntent().getStringExtra("PRODUCT_NAME"));
        List<String> parsedExtra = Arrays.asList(getIntent().getStringExtra("INGREDIENTS"));
        skincareProduct.setBrand(getIntent().getStringExtra("BRANDS"));
        skincareProduct.set__parsed_extra(parsedExtra);


        // Set data to TextViews
        productNameTv.setText(skincareProduct.getProduct_name());
        ingredientsTv.setText(skincareProduct.getIngredients());
        brands.setText(skincareProduct.getBrand());
        StringBuilder parsedExtraBuilder = new StringBuilder();
        for (String extra : skincareProduct.get__parsed_extra()) {
            parsedExtraBuilder.append(extra);
        }

       //// Set up RecyclerView for parsed extra
        List<String> ingredientsList = Arrays.asList(intent.getStringExtra("INGREDIENTS"));
       LinearLayoutManager layoutManager = new LinearLayoutManager(this);
       recyclerView.setLayoutManager(layoutManager);

        ParsedExtraAdapter adapter = new ParsedExtraAdapter(ingredientsList);

        recyclerView.setAdapter(adapter);

        // Add an OnCheckedChangeListener to the CheckBox
        CheckBox favouriteCheckBox = findViewById(R.id.favourite);
        favouriteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addToFavorites(skincareProduct);
            } else {
                removeFromFavorites(skincareProduct);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click event
                onBackPressed();
            }
        });
        // Set OnClickListener for the info button
        ImageButton infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> {
            showColorSchemeDialog();
        });
    }

    private void addToFavorites(SkincareProduct product) {
        // Create a new document in the "favourites" collection under the user's UID
        db.collection("Users")
                .document(currentUser.getUid())
                .collection("favourites")
                .document(product.getProduct_name()) // Assuming there is a unique identifier for each product
                .set(product) // Create a SkincareProduct object
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully added to favorites
                        Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(this, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showColorSchemeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make informed decisions on the ingredients");
        builder.setPositiveButton("Got it !", (dialog, which) -> {
            // your code here
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}



