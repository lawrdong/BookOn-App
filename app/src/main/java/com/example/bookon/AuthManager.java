package com.example.bookon;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREFS = "bookon_auth";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * Saves user data after a successful backend login.
     */
    public static void setLoggedIn(Context context, BookOnApi.User user) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putInt(KEY_USER_ID, user.id)
                .putString(KEY_USERNAME, user.username)
                .apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getInt(KEY_USER_ID, -1);
    }

    public static String getUsername(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getString(KEY_USERNAME, "Guest");
    }

    public static void logout(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
