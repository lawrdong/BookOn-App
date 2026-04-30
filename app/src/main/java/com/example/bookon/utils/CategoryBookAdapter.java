package com.example.bookon.utils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.data.models.Book;
import com.example.bookon.ui.activities.BookDetailActivity;

import java.util.List;

public class CategoryBookAdapter extends RecyclerView.Adapter<CategoryBookAdapter.ViewHolder> {

    private final List<Book> books;
    private final BookAdapter.OnBookClickListener onBookClickListener;

    public CategoryBookAdapter(List<Book> books, BookAdapter.OnBookClickListener onBookClickListener) {
        this.books = books;
        this.onBookClickListener = onBookClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());

        if (book.getThumbnailUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getThumbnailUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onBookClickListener != null) {
                onBookClickListener.onBookClick(book);
                return;
            }
            Intent intent = new Intent(v.getContext(), BookDetailActivity.class);
            intent.putExtra("id", book.getId());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("authors", book.getAuthors());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("thumbnailUrl", book.getThumbnailUrl());
            intent.putExtra("publishedDate", book.getPublishedDate());
            intent.putExtra("averageRating", book.getAverageRating() != null ? book.getAverageRating() : 0.0);
            intent.putExtra("ratingsCount", book.getRatingsCount() != null ? book.getRatingsCount() : 0);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
