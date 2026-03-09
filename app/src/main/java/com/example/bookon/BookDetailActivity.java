package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        TextView tvBookTitle = findViewById(R.id.tvBookTitle);
        TextView tvBookAuthor = findViewById(R.id.tvBookAuthor);
        TextView tvBookDescription = findViewById(R.id.tvBookDescription);

        Button btnAddToShelf = findViewById(R.id.btnAddToShelf);
        Button btnWriteReview = findViewById(R.id.btnWriteReview);
        Button btnFavorite = findViewById(R.id.btnFavorite);

        //nav click listeners
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, BrowseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, CommunityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //Login Tab
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        // get book data from Browse
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String authors = intent.getStringExtra("authors");
        String description = intent.getStringExtra("description");

        tvBookTitle.setText(title);
        tvBookAuthor.setText(authors);
        tvBookDescription.setText(description);

        //add to Shelf
        btnAddToShelf.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Intent shelfIntent = new Intent(this, ShelvesActivity.class);
                shelfIntent.putExtra("title", title);
                startActivity(shelfIntent);
            }
        });

        //Write a Review
        btnWriteReview.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Write Review W.I.P", Toast.LENGTH_SHORT).show();
            }
        });

        //Add to Favorites
        btnFavorite.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Favorite feature W.I.P", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn(this) ? "Account" : "Login");
        }
    }
}