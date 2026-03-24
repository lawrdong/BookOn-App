package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.utils.AuthManager;
import com.example.bookon.R;

public class MainActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        View btnStartExploring = findViewById(R.id.btnStartExploring);

        btnStartExploring.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BrowseActivity.class))
        );

        // Browse Tab
        tabBrowse.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BrowseActivity.class)));

        // Community Tab
        tabCommunity.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CommunityActivity.class)));

        // Login Tab - no more passing "this" to AuthManager
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            // No more passing "this" to AuthManager
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}