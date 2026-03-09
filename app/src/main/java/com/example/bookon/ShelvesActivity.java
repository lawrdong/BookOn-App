package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        TextView tvSelectedBookTitle = findViewById(R.id.tvSelectedBookTitle);

        //nav click listeners
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
        tvSelectedBookTitle.setText(title);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShelves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //may replace ... give option for shelf creation
        List<Shelf> shelves = new ArrayList<>();
        shelves.add(new Shelf("Favorites", "Books you love most"));
        shelves.add(new Shelf("Want to Read", "Books you plan to read"));
        shelves.add(new Shelf("Currently Reading", "Books you are reading now"));
        shelves.add(new Shelf("Read", "Books you have finished"));

        ShelfAdapter adapter = new ShelfAdapter(shelves, shelf -> {
            Toast.makeText(this, "\"" + title + "\" added to " + shelf.getName(), Toast.LENGTH_SHORT).show();
            finish();
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn(this) ? "Account" : "Login");
        }
    }
}