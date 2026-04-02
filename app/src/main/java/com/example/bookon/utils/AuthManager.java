package com.example.bookon.utils;

import com.example.bookon.ui.activities.BookDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {

    // Get the current Firebase Auth instance
    private static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    // Returns true if a user is currently logged in
    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    // Returns the current Firebase user object (or null if not logged in)
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    // Returns the current user's email (or "Guest" if not logged in)
    public static String getUsername() {
        FirebaseUser user = getAuth().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            return user.getEmail();
        }
        return "Guest";
    }

    // Returns the current user's unique Firebase ID (or null if not logged in)
    public static String getUserId() {
        FirebaseUser user = getAuth().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Logs the user out
    public static void logout() {
        getAuth().signOut();
    }
}