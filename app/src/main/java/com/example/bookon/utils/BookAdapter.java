package com.example.bookon.utils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.data.models.Book;
import com.example.bookon.ui.activities.BookDetailActivity;

import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying book items in a list.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<Book> books;

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout for each row
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(row);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);

        // Populate text views
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthors());
        
        if (book.getAverageRating() != null && book.getAverageRating() > 0) {
            holder.tvRating.setVisibility(View.VISIBLE);
            holder.tvRating.setText(String.format(Locale.getDefault(), "★ %.1f", book.getAverageRating()));
        } else {
            holder.tvRating.setVisibility(View.GONE);
        }

        // Show publication year
        if (book.getPublishedDate() != null && !book.getPublishedDate().isEmpty()) {
            String year = book.getPublishedDate().split("-")[0];
            holder.tvYear.setText(year);
            holder.tvYear.setVisibility(View.VISIBLE);
        } else {
            holder.tvYear.setVisibility(View.GONE);
        }

        // Load image using Glide library
        if (book.getThumbnailUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getThumbnailUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        // Open details on click
        holder.itemView.setOnClickListener(v -> {
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

    /**
     * Cache for view references in a single row.
     */
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvRating, tvYear;

        public BookViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvYear = itemView.findViewById(R.id.tvYear);
        }
    }
}
