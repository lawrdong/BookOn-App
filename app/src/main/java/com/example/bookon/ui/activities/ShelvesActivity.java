package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.models.Shelf;
import com.example.bookon.utils.AuthManager;
import com.example.bookon.utils.ShelfAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShelvesActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelves);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        TextView tvShelvesTitle = findViewById(R.id.tvShelvesTitle);
        TextView tvShelvesSubtitle = findViewById(R.id.tvShelvesSubtitle);
        TextView tvSelectedShelfBook = findViewById(R.id.tvSelectedShelfBook);
        Button btnCreateShelf = findViewById(R.id.btnCreateShelf);
        String selectedBookTitle = getIntent().getStringExtra("title");

        // nav click listeners
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(ShelvesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(ShelvesActivity.this, BrowseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(ShelvesActivity.this, CommunityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        btnCreateShelf.setOnClickListener(v ->
                Toast.makeText(this, "Create Shelf flow will include public or private visibility.", Toast.LENGTH_SHORT).show()
        );

        if (selectedBookTitle != null && !selectedBookTitle.isEmpty()) {
            tvShelvesTitle.setText("Choose a Shelf");
            tvShelvesSubtitle.setText("Pick where you want to place this book.");
            tvSelectedShelfBook.setText("Adding \"" + selectedBookTitle + "\" to a shelf");
            tvSelectedShelfBook.setVisibility(TextView.VISIBLE);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShelves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Shelf> shelves = new ArrayList<>();

        // TEMP: replace this single shelf with Firebase shelf data for the logged-in user
        shelves.add(new Shelf(
                "Favorites",
                "Public shelf for books the user wants to share with other readers.",
                1
        ));

        shelves.add(new Shelf(
                "Want to Read",
                "Private shelf for books saved personally by the user account.",
                6
        ));

        ShelfAdapter adapter = new ShelfAdapter(shelves, shelf -> {
            if (selectedBookTitle != null && !selectedBookTitle.isEmpty()) {
                Toast.makeText(this, "\"" + selectedBookTitle + "\" added to " + shelf.getTitle(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, shelf.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
