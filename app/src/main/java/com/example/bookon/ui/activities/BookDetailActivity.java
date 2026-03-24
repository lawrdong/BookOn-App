package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookon.utils.AuthManager;
import com.example.bookon.R;

import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Navigation setup
        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        // Book details UI elements
        ImageView ivBookCover = findViewById(R.id.ivBookCover);
        TextView tvBookTitle = findViewById(R.id.tvBookTitle);
        TextView tvBookAuthor = findViewById(R.id.tvBookAuthor);
        TextView tvBookDescription = findViewById(R.id.tvBookDescription);
        TextView tvPublishedDate = findViewById(R.id.tvPublishedDate);
        TextView tvAverageRating = findViewById(R.id.tvAverageRating);

        // Action buttons
        Button btnAddToShelf = findViewById(R.id.btnAddToShelf);
        Button btnWriteReview = findViewById(R.id.btnWriteReview);
        Button btnFavorite = findViewById(R.id.btnFavorite);

        // Nav click listeners
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

        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        // Get book data from Browse
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String authors = intent.getStringExtra("authors");
        String description = intent.getStringExtra("description");
        String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
        String publishedDate = intent.getStringExtra("publishedDate");
        double averageRating = intent.getDoubleExtra("averageRating", 0.0);
        int ratingsCount = intent.getIntExtra("ratingsCount", 0);

        // Populate UI with book data
        tvBookTitle.setText(title);

        if (authors != null) {
            tvBookAuthor.setText(getString(R.string.by_author, authors));
        }

        if (description != null && !description.isEmpty()) {
            tvBookDescription.setText(description);
        } else {
            tvBookDescription.setText("No description available");
        }

        if (publishedDate != null) {
            tvPublishedDate.setText(getString(R.string.published_date, publishedDate));
        } else {
            tvPublishedDate.setText("");
        }

        // Display average rating
        if (averageRating > 0) {
            tvAverageRating.setVisibility(View.VISIBLE);
            String ratingText = String.format(Locale.getDefault(), "★ %.1f", averageRating);
            if (ratingsCount > 0) {
                ratingText += String.format(Locale.getDefault(), " (%d ratings)", ratingsCount);
            }
            tvAverageRating.setText(ratingText);
        } else {
            tvAverageRating.setVisibility(View.GONE);
        }

        // Load book cover image
        if (thumbnailUrl != null && ivBookCover != null) {
            Glide.with(this)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivBookCover);
        }

        btnAddToShelf.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Intent shelfIntent = new Intent(this, ShelvesActivity.class);
                shelfIntent.putExtra("title", title);
                startActivity(shelfIntent);
            }
        });

        btnWriteReview.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Intent reviewIntent = new Intent(this, WriteReviewActivity.class);
                reviewIntent.putExtra("title", title);
                startActivity(reviewIntent);
            }
        });

        btnFavorite.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Favorite feature W.I.P", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Fixed: removed (this)
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}