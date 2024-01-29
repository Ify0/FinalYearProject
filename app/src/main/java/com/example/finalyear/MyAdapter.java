package com.example.finalyear;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<myViewHolder> {
    private final Context context;
    private List<SkincareProduct> dataList;

    public MyAdapter(Context context, List<SkincareProduct> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        holder.recTitle.setText(dataList.get(position).getProduct_name());
        holder.recDesc.setText(dataList.get(position).getBrand());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ActivityDetail.class);
                    intent.putExtra("Product Name", dataList.get(pos).getProduct_name());
                    intent.putExtra("Brand", dataList.get(pos).getBrand());
                    intent.putExtra("Ingredients", dataList.get(pos).getIngredients());
                    // Convert List<String> to String array
                    String[] parsedExtraArray = dataList.get(pos).get__parsed_extra().toArray(new String[0]);
                    intent.putExtra("Parsed Extra", parsedExtraArray);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(List<SkincareProduct> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}
