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
        Button btnCreateShelf = findViewById(R.id.btnCreateShelf);

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
                Toast.makeText(this, "Create Shelf flow goes here.", Toast.LENGTH_SHORT).show()
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShelves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Shelf> shelves = new ArrayList<>();

        // TEMP: replace this single shelf with Firebase shelf data for the logged-in user
        shelves.add(new Shelf(
                "Favorites",
                "Books saved by the user account.",
                1
        ));

        ShelfAdapter adapter = new ShelfAdapter(shelves, shelf -> {
            Toast.makeText(this, shelf.getTitle(), Toast.LENGTH_SHORT).show();
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