package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.data.models.Book;
import com.example.bookon.data.repositories.BookRepository;
import com.example.bookon.ui.activities.AccountActivity;
import com.example.bookon.ui.activities.BrowseActivity;
import com.example.bookon.ui.activities.CommunityActivity;
import com.example.bookon.ui.activities.LoginActivity;
import com.example.bookon.ui.activities.MainActivity;
import com.example.bookon.utils.AuthManager;
import com.example.bookon.utils.BookAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LibraryActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);

        //nav click listeners
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(LibraryActivity.this, BrowseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(LibraryActivity.this, CommunityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //Login Tab
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLibraryBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BookRepository repo = new BookRepository();

        repo.getTrendingBooks(0, 20, new BookRepository.BookCallback() {
            @Override
            public void onSuccess(List<Book> allBooks) {
                Set<String> favoriteIds = FavoritesManager.getFavoriteIds(LibraryActivity.this);

                List<Book> favoriteBooks = new ArrayList<>();
                for (Book book : allBooks) {
                    if (favoriteIds.contains(book.getId())) {
                        favoriteBooks.add(book);
                    }
                }

                BookAdapter adapter = new BookAdapter(favoriteBooks);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}