package com.example.bookon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);

        // fake login when user presses the button
        btnLogin.setOnClickListener(v -> {
            AuthManager.setLoggedIn(LoginActivity.this, true);

            Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        });
    }
}