package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tabLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        TextView tabLogin = findViewById(R.id.tabLogin);

        //Browse Tab
        tabBrowse.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BrowseActivity.class)));

        //Community Tab
        tabCommunity.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CommunityActivity.class)));

        //Login Tab
        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn(MainActivity.this)) {
                // Later: go to Account/Profile page
                // For now.. go to Community / show a placeholder
                startActivity(new Intent(MainActivity.this, CommunityActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn(this) ? "Account" : "Login");
        }
    }
}