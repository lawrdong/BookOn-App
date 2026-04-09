package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.utils.AuthManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BookReviewsActivity extends AppCompatActivity {

    private TextView tabLogin;
    private final List<ReviewItem> reviewItems = new ArrayList<>();
    private LinearLayout layoutReviewsContainer;
    private View tvNoReviews;
    private String currentTitle;
    private String currentAuthors;
    private String currentThumbnailUrl;
    private double currentAverageRating;
    private int currentRatingsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reviews);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        TextView tvReviewsTitle = findViewById(R.id.tvReviewsTitle);
        TextView tvReviewsSubtitle = findViewById(R.id.tvReviewsSubtitle);
        layoutReviewsContainer = findViewById(R.id.layoutReviewsContainer);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        Button btnSortReviews = findViewById(R.id.btnSortReviews);

        Intent intent = getIntent();
        currentTitle = intent.getStringExtra("title");
        currentAuthors = intent.getStringExtra("authors");
        currentThumbnailUrl = intent.getStringExtra("thumbnailUrl");
        currentAverageRating = intent.getDoubleExtra("averageRating", 0.0);
        currentRatingsCount = intent.getIntExtra("ratingsCount", 0);
        boolean hasMockReviews = intent.getBooleanExtra("hasMockReviews", false);

        tvReviewsTitle.setText(currentTitle != null ? currentTitle + " Reviews" : "Book Reviews");
        if (hasMockReviews) {
            tvReviewsSubtitle.setText("See what readers are saying about this specific book.");
        } else {
            tvReviewsSubtitle.setText("This book does not have any reviews yet.");
        }

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

        buildMockReviews(hasMockReviews);
        renderReviews();

        btnSortReviews.setOnClickListener(v -> showSortDialog());
        btnSortReviews.setVisibility(hasMockReviews ? View.VISIBLE : View.GONE);
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void buildMockReviews(boolean hasMockReviews) {
        reviewItems.clear();
        if (!hasMockReviews) {
            return;
        }

        reviewItems.add(new ReviewItem("Reader 1", "33m ago", 5,
                "Dummy review: I really liked this book and would recommend it to anyone looking for a thoughtful read.", 3));
        reviewItems.add(new ReviewItem("Reader 2", "2h ago", 4,
                "Dummy review: Strong pacing, memorable characters, and I would definitely talk about this one in the app.", 2));
        if (currentRatingsCount > 2) {
            reviewItems.add(new ReviewItem("Reader 3", "1d ago", 3,
                    "Dummy review: Not my favorite ever, but still worth checking out if the premise interests you.", 1));
        }
    }

    private void renderReviews() {
        layoutReviewsContainer.removeAllViews();

        if (reviewItems.isEmpty()) {
            tvNoReviews.setVisibility(View.VISIBLE);
            return;
        }

        tvNoReviews.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ReviewItem review : reviewItems) {
            View reviewView = inflater.inflate(R.layout.item_review, layoutReviewsContainer, false);
            TextView tvReviewUser = reviewView.findViewById(R.id.tvReviewUser);
            TextView tvReviewTime = reviewView.findViewById(R.id.tvReviewTime);
            TextView tvReviewStars = reviewView.findViewById(R.id.tvReviewStars);
            TextView tvReviewBody = reviewView.findViewById(R.id.tvReviewBody);
            TextView tvReviewBookTitle = reviewView.findViewById(R.id.tvReviewBookTitle);
            TextView tvReviewBookAuthor = reviewView.findViewById(R.id.tvReviewBookAuthor);
            TextView tvReviewBookRating = reviewView.findViewById(R.id.tvReviewBookRating);
            ImageView ivReviewBook = reviewView.findViewById(R.id.ivReviewBook);

            tvReviewUser.setText(review.userLabel);
            tvReviewTime.setText(review.timeLabel);
            tvReviewStars.setText(getStars(review.starCount));
            tvReviewBody.setText(review.body);
            tvReviewBookTitle.setText(currentTitle != null ? currentTitle : "Placeholder Book");
            tvReviewBookAuthor.setText(currentAuthors != null ? currentAuthors : "Author Name");
            double ratingToShow = currentAverageRating > 0 ? currentAverageRating : review.starCount;
            tvReviewBookRating.setText(String.format(Locale.getDefault(), "★ %.1f", ratingToShow));

            if (currentThumbnailUrl != null) {
                Glide.with(this)
                        .load(currentThumbnailUrl)
                        .placeholder(R.drawable.ic_book_placeholder)
                        .into(ivReviewBook);
            } else {
                ivReviewBook.setImageResource(R.drawable.ic_book_placeholder);
            }

            layoutReviewsContainer.addView(reviewView);
        }
    }

    private void showSortDialog() {
        String[] options = {"Newest", "Oldest", "Highest Rated", "Lowest Rated"};
        new AlertDialog.Builder(this)
                .setTitle("Organize reviews")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Collections.sort(reviewItems, (a, b) -> Integer.compare(b.recencyRank, a.recencyRank));
                            break;
                        case 1:
                            Collections.sort(reviewItems, Comparator.comparingInt(a -> a.recencyRank));
                            break;
                        case 2:
                            Collections.sort(reviewItems, (a, b) -> Integer.compare(b.starCount, a.starCount));
                            break;
                        case 3:
                            Collections.sort(reviewItems, Comparator.comparingInt(a -> a.starCount));
                            break;
                    }
                    renderReviews();
                })
                .show();
    }

    private String getStars(int count) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stars.append("★");
        }
        return stars.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }

    private static class ReviewItem {
        final String userLabel;
        final String timeLabel;
        final int starCount;
        final String body;
        final int recencyRank;

        ReviewItem(String userLabel, String timeLabel, int starCount, String body, int recencyRank) {
            this.userLabel = userLabel;
            this.timeLabel = timeLabel;
            this.starCount = starCount;
            this.body = body;
            this.recencyRank = recencyRank;
        }
    }
}
