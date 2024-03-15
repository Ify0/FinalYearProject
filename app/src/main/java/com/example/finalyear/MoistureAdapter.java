package com.example.finalyear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoistureAdapter extends RecyclerView.Adapter<MoistureAdapter.MoistureViewHolder> {

    private List<Product> moistureList;
    private Context context;

    public MoistureAdapter(Context context, List<Product> moistureList) {
        this.context = context;
        this.moistureList = moistureList;
    }

    @NonNull
    @Override
    public MoistureAdapter.MoistureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new MoistureAdapter.MoistureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoistureAdapter.MoistureViewHolder holder, int position) {
        Product moisture = moistureList.get(position);
        holder.productNameTextView.setText(moisture.getName());
        holder.productPriceTextView.setText(String.valueOf(moisture.getPrice()));
    }

    @Override
    public int getItemCount() {
        return moistureList.size();
    }
    public void setMoistureList(List<Product> moistureList) {
        this.moistureList = moistureList;
    }
    public List<Product> getMoistureList() {
        return moistureList;
    }
    public static class MoistureViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;

        public MoistureViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
        }
    }
}
