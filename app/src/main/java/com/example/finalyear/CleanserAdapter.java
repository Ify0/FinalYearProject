package com.example.finalyear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalyear.Product;
import java.util.List;

public class CleanserAdapter extends RecyclerView.Adapter<CleanserAdapter.CleanserViewHolder> {

    private List<Product> cleanserList;
    private Context context;

    public CleanserAdapter(Context context, List<Product> cleanserList) {
        this.context = context;
        this.cleanserList = cleanserList;
    }

    @NonNull
    @Override
    public CleanserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CleanserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CleanserViewHolder holder, int position) {
        Product cleanser = cleanserList.get(position);
        holder.productNameTextView.setText(cleanser.getName());
        holder.productPriceTextView.setText(String.valueOf(cleanser.getPrice()));
    }

    @Override
    public int getItemCount() {
        return cleanserList.size();
    }
    public void setCleanserList(List<Product> cleanserList) {
        this.cleanserList = cleanserList;
    }
    public static class CleanserViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;

        public CleanserViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
        }
    }
}

