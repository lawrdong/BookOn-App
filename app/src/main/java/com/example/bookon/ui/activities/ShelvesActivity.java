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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.models.Shelf;
import com.example.bookon.utils.AuthManager;
import com.example.bookon.utils.ShelfAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShelvesActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_SHELF_BOOK = 2001;
    private TextView tabLogin;
    private TextView tvShelfBooksPreview;
    private int selectedShelfBookCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shelf);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        TextView tvShelvesTitle = findViewById(R.id.tvShelvesTitle);
        TextView tvShelvesSubtitle = findViewById(R.id.tvShelvesSubtitle);
        TextView tvSelectedShelfBook = findViewById(R.id.tvSelectedShelfBook);
        LinearLayout layoutCreateShelfForm = findViewById(R.id.layoutCreateShelfForm);
        EditText etShelfName = findViewById(R.id.etShelfName);
        EditText etShelfDescription = findViewById(R.id.etShelfDescription);
        Button btnCreateShelf = findViewById(R.id.btnCreateShelf);
        Button btnAddBooksToShelf = findViewById(R.id.btnAddBooksToShelf);
        Button btnSaveNewShelf = findViewById(R.id.btnSaveNewShelf);
        tvShelfBooksPreview = findViewById(R.id.tvShelfBooksPreview);
        String selectedBookTitle = getIntent().getStringExtra("title");
        boolean fromAccountShelves = getIntent().getBooleanExtra("fromAccountShelves", false);

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
            tvSelectedShelfBook.setVisibility(View.VISIBLE);
        } else if (fromAccountShelves) {
            tvShelvesTitle.setText("Create a New Bookshelf");
            tvShelvesSubtitle.setText("Name your shelf, describe it, and add books to start the collection.");
            btnCreateShelf.setVisibility(View.GONE);
            layoutCreateShelfForm.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShelves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Shelf> shelves = new ArrayList<>();

        shelves.add(new Shelf(
                "Want to Read",
                "Books I plan to read",
                0
        ));

        shelves.add(new Shelf(
                "Already Read",
                "Books I've finished",
                0
        ));

        shelves.add(new Shelf(
                "Favorites",
                "My all-time favorites",
                0
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

        if (fromAccountShelves) {
            recyclerView.setVisibility(View.GONE);
        }

        btnAddBooksToShelf.setOnClickListener(v -> {
            Intent browseIntent = new Intent(this, BrowseActivity.class);
            browseIntent.putExtra("selectForPost", true);
            browseIntent.putExtra("pickerBannerText", "Select a book for this shelf. Tap any book below to add it.");
            startActivityForResult(browseIntent, REQUEST_SELECT_SHELF_BOOK);
        });

        btnSaveNewShelf.setOnClickListener(v -> {
            String shelfName = etShelfName.getText().toString().trim();
            String shelfDescription = etShelfDescription.getText().toString().trim();

            if (shelfName.isEmpty()) {
                etShelfName.setError("Add a shelf name");
                return;
            }

            if (shelfDescription.isEmpty()) {
                etShelfDescription.setError("Add a short description");
                return;
            }

            Toast.makeText(this, "Shelf creation will connect here.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_SHELF_BOOK && resultCode == RESULT_OK && data != null) {
            String selectedBookTitle = data.getStringExtra("title");
            if (selectedBookTitle != null && !selectedBookTitle.isEmpty()) {
                selectedShelfBookCount++;
                tvShelfBooksPreview.setText(selectedShelfBookCount == 1
                        ? selectedBookTitle
                        : selectedShelfBookCount + " books added");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
