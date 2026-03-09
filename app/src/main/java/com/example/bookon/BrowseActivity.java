package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

public class BrowseActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        // HOME
        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        // BROWSE (current page)
        if (tabBrowse != null) {
            tabBrowse.setOnClickListener(v -> {
                // already on Browse
            });
        }

        // COMMUNITY
        if (tabCommunity != null) {
            tabCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, CommunityActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        // LOGIN / ACCOUNT
        tabLogin = findViewById(R.id.tabLogin);

        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Books
        BookRepository repo = new BookRepository();
        List<Book> books = repo.getTrendingBooks();

        BookAdapter adapter = new BookAdapter(books);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn(this) ? "Account" : "Login");
        }
    }
}