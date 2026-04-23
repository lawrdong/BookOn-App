package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.data.models.Shelf;
import com.example.bookon.utils.AuthManager;
import com.example.bookon.R;
import com.example.bookon.utils.ShelfStorageManager;

import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private TextView tabLogin;
    private TextView tvAccountShelfCount;
    private LinearLayout layoutAccountShelvesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        tvAccountShelfCount = findViewById(R.id.tvAccountShelfCount);
        layoutAccountShelvesContainer = findViewById(R.id.layoutAccountShelvesContainer);
        Button btnAccountCreatePost = findViewById(R.id.btnAccountCreatePost);
        Button btnLogout = findViewById(R.id.btnLogout);

        //nav click listeners
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, BrowseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, CommunityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //login tab
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                // already on Account
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        btnAccountCreatePost.setOnClickListener(v -> {
            Intent shelvesIntent = new Intent(this, ShelvesActivity.class);
            shelvesIntent.putExtra("fromAccountShelves", true);
            startActivity(shelvesIntent);
        });

        btnLogout.setOnClickListener(v -> {
            AuthManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update login text when returning (Login -> Account) or after logout (Account -> Login)
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }

        // If user is logged out, kick them to Login
        if (!AuthManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        renderShelves();
    }

    private void renderShelves() {
        if (layoutAccountShelvesContainer == null) {
            return;
        }

        layoutAccountShelvesContainer.removeAllViews();
        List<Shelf> shelves = ShelfStorageManager.getShelves(this, AuthManager.getUserId());
        LayoutInflater inflater = LayoutInflater.from(this);

        if (tvAccountShelfCount != null) {
            int shelfCount = shelves.size();
            String shelfLabel = shelfCount == 1 ? "Shelf" : "Shelves";
            tvAccountShelfCount.setText(shelfCount + "\n" + shelfLabel);
        }

        if (shelves.isEmpty()) {
            TextView emptyState = new TextView(this);
            emptyState.setText("Create your first shelf to start organizing books.");
            emptyState.setTextColor(getColor(R.color.text_secondary));
            layoutAccountShelvesContainer.addView(emptyState);
            return;
        }

        for (Shelf shelf : shelves) {
            View shelfView = inflater.inflate(R.layout.item_shelf, layoutAccountShelvesContainer, false);
            TextView tvShelfTitle = shelfView.findViewById(R.id.tvShelfTitle);
            TextView tvShelfDescription = shelfView.findViewById(R.id.tvShelfDescription);
            TextView tvShelfCount = shelfView.findViewById(R.id.tvShelfCount);
            TextView btnDeleteShelf = shelfView.findViewById(R.id.btnDeleteShelf);

            tvShelfTitle.setText(shelf.getTitle());
            tvShelfDescription.setText(shelf.getDescription());
            if (shelf.getBookCount() > 0) {
                tvShelfCount.setText(shelf.getBookCount() + " books");
            } else {
                tvShelfCount.setText("0 books");
            }
            btnDeleteShelf.setVisibility(View.VISIBLE);
            btnDeleteShelf.setOnClickListener(v -> {
                ShelfStorageManager.deleteShelf(this, AuthManager.getUserId(), shelf.getTitle());
                renderShelves();
            });
            shelfView.setOnClickListener(v -> {
                Intent shelfDetailIntent = new Intent(this, ShelfDetailActivity.class);
                shelfDetailIntent.putExtra("shelfTitle", shelf.getTitle());
                startActivity(shelfDetailIntent);
            });

            layoutAccountShelvesContainer.addView(shelfView);
        }
    }
}
