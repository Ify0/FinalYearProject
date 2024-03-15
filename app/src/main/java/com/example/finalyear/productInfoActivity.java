package com.example.finalyear;

// ProductInfoActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class productInfoActivity extends AppCompatActivity {

    private TextView productNameTv, ingredientsTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        productNameTv = findViewById(R.id.detailPriority);
        ingredientsTv = findViewById(R.id.detailDesc);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String productName = intent.getStringExtra("PRODUCT_NAME");
        String ingredients = intent.getStringExtra("INGREDIENTS");

        // Set data to TextViews
        productNameTv.setText(productName);
        ingredientsTv.setText(ingredients);
    }
}

