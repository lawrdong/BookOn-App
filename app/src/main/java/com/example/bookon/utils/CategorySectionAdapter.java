package com.example.bookon.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.models.CategorySection;

import java.util.List;

public class CategorySectionAdapter extends RecyclerView.Adapter<CategorySectionAdapter.ViewHolder> {

    private final List<CategorySection> sections;
    private final BookAdapter.OnBookClickListener onBookClickListener;

    public CategorySectionAdapter(List<CategorySection> sections, BookAdapter.OnBookClickListener onBookClickListener) {
        this.sections = sections;
        this.onBookClickListener = onBookClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySection section = sections.get(position);
        holder.tvCategoryName.setText(section.getTitle());
        
        CategoryBookAdapter bookAdapter = new CategoryBookAdapter(section.getBooks(), onBookClickListener);
        holder.rvCategoryBooks.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.rvCategoryBooks.setAdapter(bookAdapter);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        RecyclerView rvCategoryBooks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            rvCategoryBooks = itemView.findViewById(R.id.rvCategoryBooks);
        }
    }
}
