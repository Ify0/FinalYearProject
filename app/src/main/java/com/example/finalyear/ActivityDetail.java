package com.example.finalyear;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyear.databinding.ActivityDetailBinding;

import java.util.Arrays;
import java.util.List;

public class ActivityDetail extends AppCompatActivity {
    private SkincareProduct skincareProduct;
    private String imageUrl = "";
    private @NonNull ActivityDetailBinding binding;

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
    }
}
