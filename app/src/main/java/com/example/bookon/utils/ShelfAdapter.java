package com.example.bookon.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.models.Shelf;

import java.util.List;

public class ShelfAdapter extends RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder> {

    private final List<Shelf> shelves;
    private final OnShelfClickListener listener;

    public interface OnShelfClickListener {
        void onShelfClick(Shelf shelf);
    }

    public ShelfAdapter(List<Shelf> shelves, OnShelfClickListener listener) {
        this.shelves = shelves;
        this.listener = listener;
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shelf, parent, false);
        return new ShelfViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
        Shelf shelf = shelves.get(position);

        holder.tvShelfTitle.setText(shelf.getTitle());
        holder.tvShelfDescription.setText(shelf.getDescription());
        holder.tvShelfCount.setText(shelf.getBookCount() + " books");

        holder.itemView.setOnClickListener(v -> listener.onShelfClick(shelf));
    }

    @Override
    public int getItemCount() {
        return shelves.size();
    }

    static class ShelfViewHolder extends RecyclerView.ViewHolder {

        TextView tvShelfTitle, tvShelfDescription, tvShelfCount;

        public ShelfViewHolder(View itemView) {
            super(itemView);
            tvShelfTitle = itemView.findViewById(R.id.tvShelfTitle);
            tvShelfDescription = itemView.findViewById(R.id.tvShelfDescription);
            tvShelfCount = itemView.findViewById(R.id.tvShelfCount);
        }
    }
}