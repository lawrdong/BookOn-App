package com.example.bookon;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREFS = "bookon_auth";
    private static final String KEY_LOGGED_IN = "logged_in";

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_LOGGED_IN, false); // DEFAULT FALSE
    }

    public static void setLoggedIn(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }
}