package com.example.finalyear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.TreatmentViewHolder> {

    private List<Product> treatmentList;
    private Context context;

    public TreatmentAdapter(Context context, List<Product> treatmentList) {
        this.context = context;
        this.treatmentList = treatmentList;
    }

    @NonNull
    @Override
    public TreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentViewHolder holder, int position) {
        Product treatment = treatmentList.get(position);
        holder.productNameTextView.setText(treatment.getName());
        holder.productPriceTextView.setText(String.valueOf(treatment.getPrice()));
    }

    @Override
    public int getItemCount() {
        return treatmentList.size();
    }

    public void setTreatmentList(List<Product> treatmentList) {
        this.treatmentList = treatmentList;
    }

    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;

        public TreatmentViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
        }
    }
}
