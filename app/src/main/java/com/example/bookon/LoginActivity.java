package com.example.bookon;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);

        //fake login to test UI
        btnLogin.setOnClickListener(v -> {
            AuthManager.setLoggedIn(this, true);
            finish(); // return to previous screen
        });
    }
}