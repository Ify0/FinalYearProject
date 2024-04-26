package com.example.finalyear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    CardView skinReader , search , scan , logout , favourite , tracker , profile ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);


        skinReader = findViewById(R.id.skinReader);
        skinReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SkinReader.class);
                startActivity(intent);
            }
        });

         search = findViewById(R.id.searchProduct);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, searchActivity.class);
                startActivity(intent);
            }
        });

       scan = findViewById(R.id.scanProduct);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });



        profile = findViewById(R.id.Proflie);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        favourite = findViewById(R.id.favourites);

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        tracker = findViewById(R.id.tracking);

        tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TrackerActivity.class);
                startActivity(intent);
            }
        });

    }
}