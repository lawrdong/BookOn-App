package com.example.bookon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<Book> books; // list of data

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    //when RecyclerView needs a new row
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Turn XML into real UI row
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(row);
    }

    //fill data into a row
    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        // placeholder
        holder.tvRating.setText("");
    }

    //# of rows
    @Override
    public int getItemCount() {
        return books.size();
    }

    //holds views inside one row
    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvRating;
        public BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}