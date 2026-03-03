package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
            if (AuthManager.isLoggedIn(this)) {
                // already on Account
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        btnLogout.setOnClickListener(v -> {
            AuthManager.logout(AccountActivity.this);

            finish();  // smooth logout
            overridePendingTransition(0, 0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update login text when returning (Login -> Account) or after logout (Account -> Login)
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn(this) ? "Account" : "Login");
        }

        // If user is logged out and somehow opened this page, kick them to Login
        if (!AuthManager.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}