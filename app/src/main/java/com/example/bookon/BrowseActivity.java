package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

public class BrowseActivity extends AppCompatActivity {

    private TextView tabLogin;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private ProgressBar progressBar;
    private EditText etSearch;
    private Button btnSearch;
    private List<Book> bookList = new ArrayList<>();
    
    private int startIndex = 0;
    private final int maxResults = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        progressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        // HOME
        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        // COMMUNITY
        if (tabCommunity != null) {
            tabCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, CommunityActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        // LOGIN / ACCOUNT
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        // SEARCH BUTTON
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            performNewSearch(query);
        });

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewBooks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(bookList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= maxResults) {
                        loadBooks();
                    }
                }
            }
        });

        loadBooks(); // Initial load (trending)
    }

    private void performNewSearch(String query) {
        currentQuery = query;
        startIndex = 0;
        isLastPage = false;
        bookList.clear();
        adapter.notifyDataSetChanged();
        loadBooks();
    }

    private void loadBooks() {
        isLoading = true;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        BookRepository repo = new BookRepository();
        BookRepository.BookCallback callback = new BookRepository.BookCallback() {
            @Override
            public void onSuccess(List<Book> books) {
                isLoading = false;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (books.isEmpty()) {
                    isLastPage = true;
                } else {
                    int insertStart = bookList.size();
                    bookList.addAll(books);
                    adapter.notifyItemRangeInserted(insertStart, books.size());
                    startIndex += maxResults;
                }
            }

            @Override
            public void onError(Throwable t) {
                isLoading = false;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(BrowseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        if (currentQuery.isEmpty()) {
            repo.getTrendingBooks(startIndex, maxResults, callback);
        } else {
            repo.searchBooks(currentQuery, startIndex, maxResults, callback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn(this) ? "Account" : "Login");
        }
    }
}
