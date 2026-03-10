package com.example.bookon;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {

    private static final String PREFS = "bookon_favorites";
    private static final String KEY_FAVORITES = "favorite_ids";

    public static Set<String> getFavoriteIds(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return new HashSet<>(sp.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }

    public static boolean isFavorite(Context context, String bookId) {
        return getFavoriteIds(context).contains(bookId);
    }

    public static void addFavorite(Context context, String bookId) {
        Set<String> favorites = getFavoriteIds(context);
        favorites.add(bookId);

        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public static void removeFavorite(Context context, String bookId) {
        Set<String> favorites = getFavoriteIds(context);
        favorites.remove(bookId);

        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }
}