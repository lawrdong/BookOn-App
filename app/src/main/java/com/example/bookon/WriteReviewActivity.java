package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WriteReviewActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        TextView tvSelectedReviewBook = findViewById(R.id.tvSelectedReviewBook);
        Spinner spinnerRating = findViewById(R.id.spinnerRating);
        EditText etReviewText = findViewById(R.id.etReviewText);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        //nav click listeners
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(WriteReviewActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(WriteReviewActivity.this, BrowseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(WriteReviewActivity.this, CommunityActivity.class);
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

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        tvSelectedReviewBook.setText(title);

        String[] ratings = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ratings
        );
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(ratingAdapter);

        btnSubmitReview.setOnClickListener(v -> {
            String selectedRating = spinnerRating.getSelectedItem().toString();
            String reviewText = etReviewText.getText().toString().trim();

            if (reviewText.isEmpty()) {
                etReviewText.setError("Please enter a review");
                return;
            }

            Toast.makeText(
                    this,
                    "Review submitted: " + selectedRating + "/5",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
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