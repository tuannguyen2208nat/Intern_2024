package com.example.intern_2024.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.intern_2024.R;
import com.example.intern_2024.model.Item;
import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.HomeViewHolder> {

    private List<Item> list;

    public RecycleViewAdapter() {
        this.list = new ArrayList<>();
    }

    public void setList(List<Item> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    public Item getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Item item = list.get(position);
        holder.time.setText(item.getTime());
        holder.detail.setText(item.getDetail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView time, detail;

        public HomeViewHolder(@NonNull View view) {
            super(view);
            time = view.findViewById(R.id.tv_time);
            detail = view.findViewById(R.id.tv_detail);
        }
    }
}