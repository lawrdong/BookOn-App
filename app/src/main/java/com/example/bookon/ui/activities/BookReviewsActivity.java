package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.data.models.Review;
import com.example.bookon.data.repositories.BookRepository;
import com.example.bookon.utils.AuthManager;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookReviewsActivity extends AppCompatActivity {

    private TextView tabLogin;
    private final List<Review> reviews = new ArrayList<>();
    private LinearLayout layoutReviewsContainer;
    private View tvNoReviews;
    private String currentBookId;
    private String currentTitle;
    private String currentAuthors;
    private String currentThumbnailUrl;
    private double currentAverageRating;
    private BookRepository repository;
    private ListenerRegistration reviewsListener;

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
        TextView btnBackReviews = findViewById(R.id.btnBackReviews);
        layoutReviewsContainer = findViewById(R.id.layoutReviewsContainer);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        Button btnSortReviews = findViewById(R.id.btnSortReviews);

        Intent intent = getIntent();
        currentBookId = intent.getStringExtra("id");
        currentTitle = intent.getStringExtra("title");
        currentAuthors = intent.getStringExtra("authors");
        currentThumbnailUrl = intent.getStringExtra("thumbnailUrl");
        currentAverageRating = intent.getDoubleExtra("averageRating", 0.0);
        repository = new BookRepository();

        tvReviewsTitle.setText(currentTitle != null ? currentTitle + " Reviews" : "Book Reviews");
        tvReviewsSubtitle.setText("See what readers are saying about this specific book.");

        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        btnBackReviews.setOnClickListener(v -> {
            Intent backIntent = new Intent(this, BookDetailActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(backIntent);
        });
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        fetchReviews();

        btnSortReviews.setOnClickListener(v -> showSortDialog());
    }

    private void fetchReviews() {
        if (currentBookId == null) return;
        if (reviewsListener != null) reviewsListener.remove();

        reviewsListener = repository.listenToReviewsForBook(currentBookId, new BookRepository.ReviewCallback() {
            @Override
            public void onSuccess(List<Review> fetchedReviews) {
                reviews.clear();
                reviews.addAll(fetchedReviews);
                renderReviews();
                findViewById(R.id.btnSortReviews).setVisibility(reviews.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                tvNoReviews.setVisibility(View.VISIBLE);
                if (tvNoReviews instanceof TextView) {
                    ((TextView) tvNoReviews).setText("Error loading reviews");
                }
            }
        });
    }

    private void deleteReview(Review review) {
        if (review.getId() == null) {
            Toast.makeText(this, "Error: Review ID is null. Cannot delete.", Toast.LENGTH_LONG).show();
            return;
        }

        // The listener will handle the UI update automatically when Firestore confirms deletion
        repository.deleteReview(review.getId(), (success, message) -> {
            if (!success) {
                Toast.makeText(this, "Delete failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void renderReviews() {
        layoutReviewsContainer.removeAllViews();

        if (reviews.isEmpty()) {
            tvNoReviews.setVisibility(View.VISIBLE);
            return;
        }

        tvNoReviews.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Review review : reviews) {
            View reviewView = inflater.inflate(R.layout.item_review, layoutReviewsContainer, false);
            TextView tvReviewAvatar = reviewView.findViewById(R.id.tvReviewAvatar);
            ImageView ivReviewUserPic = reviewView.findViewById(R.id.ivReviewUserPic);
            TextView tvReviewUser = reviewView.findViewById(R.id.tvReviewUser);
            TextView tvReviewTime = reviewView.findViewById(R.id.tvReviewTime);
            TextView tvReviewStars = reviewView.findViewById(R.id.tvReviewStars);
            TextView tvReviewBody = reviewView.findViewById(R.id.tvReviewBody);
            TextView tvReviewBookTitle = reviewView.findViewById(R.id.tvReviewBookTitle);
            TextView tvReviewBookAuthor = reviewView.findViewById(R.id.tvReviewBookAuthor);
            TextView tvReviewBookRating = reviewView.findViewById(R.id.tvReviewBookRating);
            ImageView ivReviewBook = reviewView.findViewById(R.id.ivReviewBook);
            ImageView btnDeleteReview = reviewView.findViewById(R.id.btnDeleteReview);

            String reviewUserId = review.getUserId();
            String currentUserId = AuthManager.getUserId();

            if (currentUserId != null && currentUserId.equals(reviewUserId)) {
                btnDeleteReview.setVisibility(View.VISIBLE);
                btnDeleteReview.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Review")
                            .setMessage("Are you sure you want to delete this review?")
                            .setPositiveButton("Delete", (dialog, which) -> deleteReview(review))
                            .setNegativeButton("Cancel", null)
                            .show();
                });
            } else {
                btnDeleteReview.setVisibility(View.GONE);
            }

            String userName = review.getUserName() != null ? review.getUserName() : "Reader";
            tvReviewUser.setText(userName);
            tvReviewAvatar.setText(userName.substring(0, 1).toUpperCase());
            
            if (review.getUserProfilePic() != null && !review.getUserProfilePic().isEmpty()) {
                ivReviewUserPic.setVisibility(View.VISIBLE);
                Glide.with(this).load(review.getUserProfilePic()).circleCrop().into(ivReviewUserPic);
            } else {
                ivReviewUserPic.setVisibility(View.GONE);
            }

            tvReviewTime.setText(formatDate(review.getCreatedAt()));
            tvReviewStars.setText(getStars(review.getRating()));
            tvReviewBody.setText(review.getReviewText());
            tvReviewBookTitle.setText(currentTitle != null ? currentTitle : review.getBookTitle());
            tvReviewBookAuthor.setText(currentAuthors != null ? currentAuthors : "Author Name");
            double ratingToShow = currentAverageRating > 0 ? currentAverageRating : review.getRating();
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
        String[] options = {"Newest", "Highest Rated", "Lowest Rated"};
        new AlertDialog.Builder(this)
                .setTitle("Organize reviews")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Collections.sort(reviews, (a, b) -> {
                                if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                                return b.getCreatedAt().compareTo(a.getCreatedAt());
                            });
                            break;
                        case 1:
                            Collections.sort(reviews, (a, b) -> Integer.compare(b.getRating(), a.getRating()));
                            break;
                        case 2:
                            Collections.sort(reviews, Comparator.comparingInt(Review::getRating));
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

    private String formatDate(String timestampStr) {
        if (timestampStr == null) return "Recently";
        try {
            long time = Long.parseLong(timestampStr);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(time));
        } catch (NumberFormatException e) {
            return "Recently";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reviewsListener != null) reviewsListener.remove();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
