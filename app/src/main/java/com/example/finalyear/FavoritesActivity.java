package com.example.finalyear;

// FavoritesActivity.java
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private FavoritesAdapter favoritesAdapter;
    private List<SkincareProduct> favoritesList;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        favoritesList = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(this, favoritesList);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFavorites.setAdapter(favoritesAdapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Load favorites from the database
        loadFavorites();
    }

    private void loadFavorites() {
        if (currentUser != null) {
            db.collection("Users")
                    .document(currentUser.getUid())
                    .collection("favourites")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            favoritesList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SkincareProduct product = document.toObject(SkincareProduct.class);
                                favoritesList.add(product);
                            }
                            favoritesAdapter.notifyDataSetChanged();
                        } else {
                            // Handle failure
                        }
                    });
        }
    }
}

