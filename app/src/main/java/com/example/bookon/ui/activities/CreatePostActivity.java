package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.models.Post;
import com.example.bookon.data.repositories.BookRepository;
import com.example.bookon.utils.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreatePostActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_BOOK = 1001;
    private TextView tabLogin;
    private LinearLayout layoutSelectedBookCard;
    private TextView tvSelectedBookTitle;
    private TextView tvSelectedBookAuthor;
    private TextView tvSelectedBookSource;
    
    private String selectedBookId;
    private String selectedBookThumbnail;
    private String selectedBookDescription;
    private String selectedBookPublishedDate;
    private double selectedBookAverageRating;
    private BookRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        if (!AuthManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to create a post", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        repository = new BookRepository();
        
        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        EditText etPostTitle = findViewById(R.id.etPostTitle);
        EditText etPostBody = findViewById(R.id.etPostBody);
        layoutSelectedBookCard = findViewById(R.id.layoutSelectedBookCard);
        tvSelectedBookTitle = findViewById(R.id.tvSelectedBookTitle);
        tvSelectedBookAuthor = findViewById(R.id.tvSelectedBookAuthor);
        tvSelectedBookSource = findViewById(R.id.tvSelectedBookSource);
        Button btnAddBook = findViewById(R.id.btnAddBook);
        Button btnPublishPost = findViewById(R.id.btnPublishPost);
        TextView btnBackToCommunity = findViewById(R.id.btnBackToCommunity);

        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        tabLogin.setOnClickListener(v -> navigateTo(AccountActivity.class));
        btnBackToCommunity.setOnClickListener(v -> {
            Intent backIntent = new Intent(this, CommunityActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(backIntent);
        });

        btnAddBook.setOnClickListener(v -> {
            Intent browseIntent = new Intent(this, BrowseActivity.class);
            browseIntent.putExtra("selectForPost", true);
            browseIntent.putExtra("pickerBannerText", "Select a book to link to your post");
            startActivityForResult(browseIntent, REQUEST_SELECT_BOOK);
        });

        btnPublishPost.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String body = etPostBody.getText().toString().trim();

            if (title.isEmpty()) {
                etPostTitle.setError("Add a title");
                return;
            }

            if (body.isEmpty()) {
                etPostBody.setError("Share something with the community");
                return;
            }

            publishPostToFirebase(title, body);
        });
    }

    private void publishPostToFirebase(String title, String body) {
        String userId = AuthManager.getUserId();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userName = "Reader";
        if (user != null && user.getEmail() != null) {
            userName = user.getEmail().split("@")[0];
        }

        Post post = new Post(userId, userName, title, body, String.valueOf(System.currentTimeMillis()));
        if (selectedBookId != null) {
            post.setBookId(selectedBookId);
            post.setBookTitle(tvSelectedBookTitle.getText().toString());
            post.setBookAuthor(tvSelectedBookAuthor.getText().toString());
            post.setBookThumbnail(selectedBookThumbnail);
            post.setBookDescription(selectedBookDescription);
            post.setBookPublishedDate(selectedBookPublishedDate);
            post.setBookAverageRating(selectedBookAverageRating);
        }

        Button btnPublishPost = findViewById(R.id.btnPublishPost);
        btnPublishPost.setEnabled(false);
        btnPublishPost.setAlpha(0.5f);

        repository.publishPost(post, (success, message) -> {
            if (success) {
                Toast.makeText(this, "Post published!", Toast.LENGTH_SHORT).show();
                navigateTo(CommunityActivity.class);
                finish();
            } else {
                btnPublishPost.setEnabled(true);
                btnPublishPost.setAlpha(1.0f);
                Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSelectedBookCard(String id, String title, String author, String thumbnail, String description, String date, double rating) {
        layoutSelectedBookCard.setVisibility(View.VISIBLE);
        tvSelectedBookTitle.setText(title);
        tvSelectedBookAuthor.setText(author);
        tvSelectedBookSource.setText("Linked Book");
        selectedBookId = id;
        selectedBookThumbnail = thumbnail;
        selectedBookDescription = description;
        selectedBookPublishedDate = date;
        selectedBookAverageRating = rating;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_BOOK && resultCode == RESULT_OK && data != null) {
            String id = data.getStringExtra("id");
            String title = data.getStringExtra("title");
            String author = data.getStringExtra("authors");
            String thumbnail = data.getStringExtra("thumbnailUrl");
            String description = data.getStringExtra("description");
            String date = data.getStringExtra("publishedDate");
            double rating = data.getDoubleExtra("averageRating", 0.0);
            
            if (title != null && !title.isEmpty()) {
                updateSelectedBookCard(
                        id,
                        title,
                        author != null && !author.isEmpty() ? author : "Unknown Author",
                        thumbnail,
                        description,
                        date,
                        rating);
            }
        }
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
