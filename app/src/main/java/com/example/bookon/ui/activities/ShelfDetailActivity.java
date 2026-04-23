package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.data.models.Shelf;
import com.example.bookon.data.models.ShelfBook;
import com.example.bookon.utils.AuthManager;
import com.example.bookon.utils.ShelfStorageManager;

import java.util.Locale;

public class ShelfDetailActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_BOOK_TO_SHELF = 3001;

    private TextView tabLogin;
    private String shelfTitle;
    private TextView tvShelfDetailTitle;
    private TextView tvShelfDetailSubtitle;
    private TextView btnEditShelf;
    private TextView tvEditShelfNameLabel;
    private TextView tvEditShelfDescriptionLabel;
    private EditText etShelfDetailTitle;
    private EditText etShelfDetailSubtitle;
    private Button btnSaveShelfDetails;
    private Button btnCancelShelfEdit;
    private Button btnAddBookToShelf;
    private LinearLayout layoutShelfBooksContainer;
    private TextView tvShelfEmptyState;
    private String loadedShelfTitle = "";
    private String loadedShelfDescription = "";
    private boolean isEditingShelf = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_detail);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        TextView btnBackShelf = findViewById(R.id.btnBackShelf);
        tvShelfDetailTitle = findViewById(R.id.tvShelfDetailTitle);
        tvShelfDetailSubtitle = findViewById(R.id.tvShelfDetailSubtitle);
        btnEditShelf = findViewById(R.id.btnEditShelf);
        tvEditShelfNameLabel = findViewById(R.id.tvEditShelfNameLabel);
        tvEditShelfDescriptionLabel = findViewById(R.id.tvEditShelfDescriptionLabel);
        etShelfDetailTitle = findViewById(R.id.etShelfDetailTitle);
        etShelfDetailSubtitle = findViewById(R.id.etShelfDetailSubtitle);
        layoutShelfBooksContainer = findViewById(R.id.layoutShelfBooksContainer);
        tvShelfEmptyState = findViewById(R.id.tvShelfEmptyState);
        btnSaveShelfDetails = findViewById(R.id.btnSaveShelfDetails);
        btnCancelShelfEdit = findViewById(R.id.btnCancelShelfEdit);
        btnAddBookToShelf = findViewById(R.id.btnAddBookToShelf);

        shelfTitle = getIntent().getStringExtra("shelfTitle");

        tabHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        tabBrowse.setOnClickListener(v -> navigateTo(BrowseActivity.class));
        tabCommunity.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        tabLogin.setOnClickListener(v -> navigateTo(AccountActivity.class));
        btnBackShelf.setOnClickListener(v -> finish());

        btnEditShelf.setOnClickListener(v -> setEditingShelf(true));
        btnSaveShelfDetails.setOnClickListener(v -> saveShelfDetails());
        btnCancelShelfEdit.setOnClickListener(v -> {
            etShelfDetailTitle.setText(loadedShelfTitle);
            etShelfDetailSubtitle.setText(loadedShelfDescription);
            setEditingShelf(false);
        });

        btnAddBookToShelf.setOnClickListener(v -> {
            Intent browseIntent = new Intent(this, BrowseActivity.class);
            browseIntent.putExtra("selectForPost", true);
            browseIntent.putExtra("pickerBannerText", "Select a book to add to this shelf.");
            startActivityForResult(browseIntent, REQUEST_ADD_BOOK_TO_SHELF);
        });

        setEditingShelf(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
        renderShelf();
    }

    private void renderShelf() {
        Shelf shelf = ShelfStorageManager.getShelf(this, AuthManager.getUserId(), shelfTitle);
        if (shelf == null) {
            finish();
            return;
        }

        loadedShelfTitle = shelf.getTitle() != null ? shelf.getTitle() : "";
        loadedShelfDescription = shelf.getDescription() != null ? shelf.getDescription() : "";
        tvShelfDetailTitle.setText(loadedShelfTitle);
        tvShelfDetailSubtitle.setText(loadedShelfDescription);
        etShelfDetailTitle.setText(loadedShelfTitle);
        etShelfDetailSubtitle.setText(loadedShelfDescription);
        layoutShelfBooksContainer.removeAllViews();

        if (shelf.getBooks().isEmpty()) {
            tvShelfEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        tvShelfEmptyState.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (ShelfBook book : shelf.getBooks()) {
            View row = inflater.inflate(R.layout.item_shelf_book, layoutShelfBooksContainer, false);
            ImageView ivShelfBookCover = row.findViewById(R.id.ivShelfBookCover);
            TextView tvShelfBookTitle = row.findViewById(R.id.tvShelfBookTitle);
            TextView tvShelfBookAuthor = row.findViewById(R.id.tvShelfBookAuthor);
            TextView tvShelfBookRating = row.findViewById(R.id.tvShelfBookRating);
            TextView tvShelfBookYear = row.findViewById(R.id.tvShelfBookYear);
            TextView btnRemoveShelfBook = row.findViewById(R.id.btnRemoveShelfBook);

            tvShelfBookTitle.setText(book.getTitle());
            tvShelfBookAuthor.setText(book.getAuthors());

            if (book.getAverageRating() > 0) {
                tvShelfBookRating.setVisibility(View.VISIBLE);
                tvShelfBookRating.setText(String.format(Locale.getDefault(), "★ %.1f", book.getAverageRating()));
            } else {
                tvShelfBookRating.setVisibility(View.GONE);
            }

            String publishedDate = book.getPublishedDate();
            if (publishedDate != null && !publishedDate.isEmpty()) {
                tvShelfBookYear.setVisibility(View.VISIBLE);
                tvShelfBookYear.setText(publishedDate.length() >= 4 ? publishedDate.substring(0, 4) : publishedDate);
            } else {
                tvShelfBookYear.setVisibility(View.GONE);
            }

            Glide.with(this)
                    .load(book.getThumbnailUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(ivShelfBookCover);

            btnRemoveShelfBook.setVisibility(isEditingShelf ? View.VISIBLE : View.GONE);
            btnRemoveShelfBook.setOnClickListener(v -> {
                ShelfStorageManager.removeBookFromShelf(this, AuthManager.getUserId(), shelfTitle, book.getTitle());
                renderShelf();
            });

            layoutShelfBooksContainer.addView(row);
        }
    }

    private void setEditingShelf(boolean editing) {
        isEditingShelf = editing;
        tvShelfDetailTitle.setVisibility(editing ? View.GONE : View.VISIBLE);
        tvShelfDetailSubtitle.setVisibility(editing ? View.GONE : View.VISIBLE);
        btnEditShelf.setVisibility(editing ? View.GONE : View.VISIBLE);

        tvEditShelfNameLabel.setVisibility(editing ? View.VISIBLE : View.GONE);
        tvEditShelfDescriptionLabel.setVisibility(editing ? View.VISIBLE : View.GONE);
        etShelfDetailTitle.setVisibility(editing ? View.VISIBLE : View.GONE);
        etShelfDetailSubtitle.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnSaveShelfDetails.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnCancelShelfEdit.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnAddBookToShelf.setVisibility(editing ? View.VISIBLE : View.GONE);

        if (layoutShelfBooksContainer != null) {
            renderShelf();
        }
    }

    private void saveShelfDetails() {
        String updatedTitle = etShelfDetailTitle.getText().toString().trim();
        String updatedDescription = etShelfDetailSubtitle.getText().toString().trim();

        if (updatedTitle.isEmpty()) {
            etShelfDetailTitle.setError("Add a shelf name");
            return;
        }

        if (updatedDescription.isEmpty()) {
            etShelfDetailSubtitle.setError("Add a short description");
            return;
        }

        if (updatedTitle.equals(loadedShelfTitle) && updatedDescription.equals(loadedShelfDescription)) {
            Toast.makeText(this, "Update the shelf first", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = ShelfStorageManager.updateShelfDetails(
                this,
                AuthManager.getUserId(),
                shelfTitle,
                updatedTitle,
                updatedDescription
        );

        if (updated) {
            shelfTitle = updatedTitle;
            loadedShelfTitle = updatedTitle;
            loadedShelfDescription = updatedDescription;
            Toast.makeText(this, "Shelf updated", Toast.LENGTH_SHORT).show();
            setEditingShelf(false);
            renderShelf();
        } else {
            Toast.makeText(this, "A shelf with that name already exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_BOOK_TO_SHELF && resultCode == RESULT_OK && data != null) {
            String selectedBookTitle = data.getStringExtra("title");
            if (selectedBookTitle != null && !selectedBookTitle.isEmpty()) {
                ShelfBook selectedBook = new ShelfBook(
                        selectedBookTitle,
                        data.getStringExtra("authors"),
                        data.getStringExtra("thumbnailUrl"),
                        data.getStringExtra("publishedDate"),
                        data.getDoubleExtra("averageRating", 0.0)
                );
                boolean added = ShelfStorageManager.addBookToShelf(this, AuthManager.getUserId(), shelfTitle, selectedBook);
                Toast.makeText(this,
                        added ? "\"" + selectedBookTitle + "\" added"
                                : "\"" + selectedBookTitle + "\" is already in this shelf",
                        Toast.LENGTH_SHORT).show();
                renderShelf();
            }
        }
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
