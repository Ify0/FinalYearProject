package com.example.finalyear;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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

        // Implement swipe-to-dismiss functionality
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    SkincareProduct product = favoritesList.get(position);
                    favoritesAdapter.removeFromFavorites(product);
                }
            }
        };
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click event
                onBackPressed();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewFavorites);

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

