package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabCommunity = findViewById(R.id.tabCommunity);

        //nav click listeners
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabCommunity.setOnClickListener(v -> {
            startActivity(new Intent(BrowseActivity.this, CommunityActivity.class));
            finish();
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BookRepository repo = new BookRepository();
        List<Book> books = repo.getTrendingBooks();

        BookAdapter adapter = new BookAdapter(books);
        recyclerView.setAdapter(adapter);

    }
}
