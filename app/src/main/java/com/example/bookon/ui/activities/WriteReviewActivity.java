package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.data.repositories.BookRepository;
import com.example.bookon.utils.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class WriteReviewActivity extends AppCompatActivity {

    private TextView tabLogin;
    private String bookId;
    private String bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // Check login status first
        if (!AuthManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to write a review", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        ImageView ivReviewBookCover = findViewById(R.id.ivReviewBookCover);
        TextView tvSelectedReviewBook = findViewById(R.id.tvSelectedReviewBook);
        TextView tvSelectedReviewAuthor = findViewById(R.id.tvSelectedReviewAuthor);
        TextView tvSelectedReviewMeta = findViewById(R.id.tvSelectedReviewMeta);
        Spinner spinnerRating = findViewById(R.id.spinnerRating);
        EditText etReviewText = findViewById(R.id.etReviewText);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);
        Button btnCheckReviews = findViewById(R.id.btnCheckReviews);
        TextView btnBackToBook = findViewById(R.id.btnBackToBookWrite);

        // Navigation
        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        tabLogin.setOnClickListener(v -> navigateTo(AccountActivity.class));
        btnBackToBook.setOnClickListener(v -> finish());

        // Get book data from Intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("id");
        bookTitle = intent.getStringExtra("title");
        String authors = intent.getStringExtra("authors");
        String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
        String publishedDate = intent.getStringExtra("publishedDate");
        double averageRating = intent.getDoubleExtra("averageRating", 0.0);

        tvSelectedReviewBook.setText(bookTitle != null ? bookTitle : "Book Title");
        tvSelectedReviewAuthor.setText(authors != null && !authors.isEmpty() ? authors : "Unknown Author");

        if (averageRating > 0) {
            tvSelectedReviewMeta.setVisibility(View.VISIBLE);
            tvSelectedReviewMeta.setText(String.format(Locale.getDefault(), "★ %.1f", averageRating));
        } else if (publishedDate != null && !publishedDate.isEmpty()) {
            tvSelectedReviewMeta.setVisibility(View.VISIBLE);
            tvSelectedReviewMeta.setText(publishedDate.split("-")[0]);
        } else {
            tvSelectedReviewMeta.setText("");
            tvSelectedReviewMeta.setVisibility(View.GONE);
        }

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivReviewBookCover);
        }

        // Setup Rating Spinner
        String[] ratings = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ratings);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(ratingAdapter);

        // Submit Button Logic
        btnSubmitReview.setOnClickListener(v -> {
            String reviewText = etReviewText.getText().toString().trim();
            int rating = Integer.parseInt(spinnerRating.getSelectedItem().toString());

            if (reviewText.isEmpty()) {
                etReviewText.setError("Please enter a review");
                return;
            }

            submitReviewToBackend(rating, reviewText);
        });

        btnCheckReviews.setOnClickListener(v -> {
            Intent reviewsIntent = new Intent(this, BookReviewsActivity.class);
            reviewsIntent.putExtra("id", bookId);
            reviewsIntent.putExtra("title", bookTitle);
            reviewsIntent.putExtra("authors", authors);
            reviewsIntent.putExtra("thumbnailUrl", thumbnailUrl);
            reviewsIntent.putExtra("averageRating", averageRating);
            startActivity(reviewsIntent);
        });
    }

    private void submitReviewToBackend(int rating, String reviewText) {
        String userId = AuthManager.getUserId();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userName = "Reader";
        String userProfilePic = null;

        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                userName = currentUser.getDisplayName();
            } else if (currentUser.getEmail() != null) {
                userName = currentUser.getEmail().split("@")[0];
            }
            if (currentUser.getPhotoUrl() != null) {
                userProfilePic = currentUser.getPhotoUrl().toString();
            }
        }
        
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setAlpha(0.5f);

        BookRepository repository = new BookRepository();
        repository.postReview(userId, userName, userProfilePic, bookId, bookTitle, rating, reviewText, (success, message) -> {
            if (success) {
                finish();
            } else {
                btnSubmitReview.setEnabled(true);
                btnSubmitReview.setAlpha(1.0f);
                Toast.makeText(WriteReviewActivity.this, "Failed to post review: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
