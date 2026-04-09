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
import com.example.bookon.utils.AuthManager;

public class CreatePostActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_BOOK = 1001;
    private TextView tabLogin;
    private LinearLayout layoutSelectedBookCard;
    private TextView tvSelectedBookTitle;
    private TextView tvSelectedBookAuthor;
    private TextView tvSelectedBookSource;

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

        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        tabLogin.setOnClickListener(v -> navigateTo(AccountActivity.class));

        btnAddBook.setOnClickListener(v -> {
            Intent browseIntent = new Intent(this, BrowseActivity.class);
            browseIntent.putExtra("selectForPost", true);
            startActivityForResult(browseIntent, REQUEST_SELECT_BOOK);
        });

        btnPublishPost.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String body = etPostBody.getText().toString().trim();
            String linkedBookTitle = tvSelectedBookTitle.getText().toString().trim();

            if (title.isEmpty()) {
                etPostTitle.setError("Add a title");
                return;
            }

            if (body.isEmpty()) {
                etPostBody.setError("Share something with the community");
                return;
            }

            boolean hasLinkedBook = !linkedBookTitle.equals("No book selected yet");
            String message = hasLinkedBook
                    ? "Post with linked book will connect here."
                    : "Post publishing flow will connect here.";

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            navigateTo(CommunityActivity.class);
            finish();
        });
    }

    private void updateSelectedBookCard(String title, String author) {
        layoutSelectedBookCard.setVisibility(View.VISIBLE);
        tvSelectedBookTitle.setText(title);
        tvSelectedBookAuthor.setText(author);
        tvSelectedBookSource.setText("Added from Browse");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_BOOK && resultCode == RESULT_OK && data != null) {
            String selectedTitle = data.getStringExtra("title");
            String selectedAuthor = data.getStringExtra("authors");
            if (selectedTitle != null && !selectedTitle.isEmpty()) {
                updateSelectedBookCard(
                        selectedTitle,
                        selectedAuthor != null && !selectedAuthor.isEmpty()
                                ? selectedAuthor
                                : "Author not available");
            }
        }
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
