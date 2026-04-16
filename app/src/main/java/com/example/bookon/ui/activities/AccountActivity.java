package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.utils.AuthManager;
import com.example.bookon.R;

public class AccountActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
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
        }
    }
}
