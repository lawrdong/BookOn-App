package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.api.BookOnApi;
import com.example.bookon.utils.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        TextView tvSelectedReviewBook = findViewById(R.id.tvSelectedReviewBook);
        Spinner spinnerRating = findViewById(R.id.spinnerRating);
        EditText etReviewText = findViewById(R.id.etReviewText);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        // Navigation
        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        tabLogin.setOnClickListener(v -> navigateTo(AccountActivity.class));

        // Get book data from Intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("id");
        bookTitle = intent.getStringExtra("title");
        tvSelectedReviewBook.setText(bookTitle);

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
    }

    private void submitReviewToBackend(int rating, String reviewText) {
        String userId = AuthManager.getUserId();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BookOnApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookOnApi api = retrofit.create(BookOnApi.class);

        // Prepare the request
        BookOnApi.ReviewRequest request = new BookOnApi.ReviewRequest(
                userId, bookId, bookTitle, rating, reviewText
        );

        // Send the request
        api.postReview(request).enqueue(new Callback<BookOnApi.SimpleResponse>() {
            @Override
            public void onResponse(Call<BookOnApi.SimpleResponse> call, Response<BookOnApi.SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Toast.makeText(WriteReviewActivity.this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous screen
                } else {
                    Toast.makeText(WriteReviewActivity.this, "Failed to post review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookOnApi.SimpleResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error posting review: " + t.getMessage());
                Toast.makeText(WriteReviewActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
