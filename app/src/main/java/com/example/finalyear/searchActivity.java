package com.example.finalyear;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.finalyear.databinding.ActivitySearchBinding;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class searchActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private List<SkincareProduct> dataList;
    private MyAdapter adapter;
    private @NonNull ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.search.clearFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter(this, dataList);
        binding.recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    SkincareProduct skincareProduct = itemSnapshot.getValue(SkincareProduct.class);
                    if (skincareProduct != null) {
                        dataList.add(skincareProduct);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                dialog.dismiss();
            }
        });


        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    private void searchList(String text) {
        List<SkincareProduct> searchList = new ArrayList<>();
        for (SkincareProduct skincareProduct : dataList) {
            if (skincareProduct.getProduct_name() != null &&
                    skincareProduct.getProduct_name().toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) {
                searchList.add(skincareProduct);
            }
        }
        adapter.searchDataList(searchList);
    }
}

