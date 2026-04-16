package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookon.FavoritesManager;
import com.example.bookon.R;
import com.example.bookon.utils.AuthManager;

import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tabLogin;
    private static final String[] MOCK_REVIEW_TITLES = {
            "The Midnight Library",
            "Project Hail Mary",
            "Frankenstein, Or, The Modern Prometheus"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        ImageView ivBookCover = findViewById(R.id.ivBookCover);
        TextView tvBookTitle = findViewById(R.id.tvBookTitle);
        TextView tvBookAuthor = findViewById(R.id.tvBookAuthor);
        TextView tvBookDescription = findViewById(R.id.tvBookDescription);
        TextView tvPublishedDate = findViewById(R.id.tvPublishedDate);
        TextView tvAverageRating = findViewById(R.id.tvAverageRating);

        Button btnAddToShelf = findViewById(R.id.btnAddToShelf);
        Button btnWriteReview = findViewById(R.id.btnWriteReview);
        Button btnCheckReviews = findViewById(R.id.btnCheckReviews);
        Button btnFavorite = findViewById(R.id.btnFavorite);

        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String authors = intent.getStringExtra("authors");
        String description = intent.getStringExtra("description");
        String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
        String publishedDate = intent.getStringExtra("publishedDate");
        double averageRating = intent.getDoubleExtra("averageRating", 0.0);
        int ratingsCount = intent.getIntExtra("ratingsCount", 0);

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
                reviewIntent.putExtra("id", intent.getStringExtra("id"));
                reviewIntent.putExtra("title", title);
                reviewIntent.putExtra("authors", authors);
                reviewIntent.putExtra("thumbnailUrl", thumbnailUrl);
                reviewIntent.putExtra("publishedDate", publishedDate);
                reviewIntent.putExtra("averageRating", averageRating);
                startActivity(reviewIntent);
            }
        });

        btnCheckReviews.setOnClickListener(v -> {
            Intent reviewsIntent = new Intent(this, BookReviewsActivity.class);
            reviewsIntent.putExtra("id", intent.getStringExtra("id"));
            reviewsIntent.putExtra("title", title);
            reviewsIntent.putExtra("authors", authors);
            reviewsIntent.putExtra("thumbnailUrl", thumbnailUrl);
            reviewsIntent.putExtra("averageRating", averageRating);
            reviewsIntent.putExtra("ratingsCount", ratingsCount);
            reviewsIntent.putExtra("hasMockReviews", hasMockReviewsForTitle(title));
            startActivity(reviewsIntent);
        });

        btnFavorite.setOnClickListener(v -> {
            if (!AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                String bookId = intent.getStringExtra("id");

                if (FavoritesManager.isFavorite(this, bookId)) {
                    FavoritesManager.removeFavorite(this, bookId);
                    btnFavorite.setText("Favorite");
                } else {
                    FavoritesManager.addFavorite(this, bookId);
                    btnFavorite.setText("Favorited");
                }
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private boolean hasMockReviewsForTitle(String title) {
        if (title == null) {
            return false;
        }
        for (String reviewTitle : MOCK_REVIEW_TITLES) {
            if (reviewTitle.equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
