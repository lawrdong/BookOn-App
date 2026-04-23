package com.example.bookon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bookon.data.models.Shelf;
import com.example.bookon.data.models.ShelfBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShelfStorageManager {

    private static final String PREFS_NAME = "bookon_shelves";
    private static final String KEY_PREFIX = "user_shelves_";

    private ShelfStorageManager() {
    }

    public static List<Shelf> getShelves(Context context, String userId) {
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PREFIX + userId, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        List<Shelf> shelves = new ArrayList<>();
        try {
            JSONArray shelfArray = new JSONArray(json);
            for (int i = 0; i < shelfArray.length(); i++) {
                JSONObject shelfObject = shelfArray.getJSONObject(i);
                JSONArray booksArray = shelfObject.optJSONArray("books");
                List<ShelfBook> books = new ArrayList<>();
                if (booksArray != null) {
                    for (int j = 0; j < booksArray.length(); j++) {
                        Object bookEntry = booksArray.opt(j);
                        if (bookEntry instanceof JSONObject) {
                            JSONObject bookObject = (JSONObject) bookEntry;
                            books.add(new ShelfBook(
                                    bookObject.optString("title"),
                                    bookObject.optString("authors", "Unknown Author"),
                                    bookObject.optString("thumbnailUrl"),
                                    bookObject.optString("publishedDate"),
                                    bookObject.optDouble("averageRating", 0.0)
                            ));
                        } else {
                            String title = booksArray.optString(j);
                            if (title != null && !title.isEmpty()) {
                                books.add(new ShelfBook(title, "Unknown Author", "", "", 0.0));
                            }
                        }
                    }
                }

                shelves.add(new Shelf(
                        shelfObject.optString("title"),
                        shelfObject.optString("description"),
                        books
                ));
            }
        } catch (JSONException e) {
            return new ArrayList<>();
        }

        if (isLegacyPlaceholderSet(shelves)) {
            saveShelves(context, userId, new ArrayList<>());
            return new ArrayList<>();
        }

        return shelves;
    }

    public static void saveShelf(Context context, String userId, Shelf newShelf) {
        List<Shelf> shelves = getShelves(context, userId);
        shelves.add(newShelf);
        saveShelves(context, userId, shelves);
    }

    public static Shelf getShelf(Context context, String userId, String shelfTitle) {
        List<Shelf> shelves = getShelves(context, userId);
        for (Shelf shelf : shelves) {
            if (shelf.getTitle().equalsIgnoreCase(shelfTitle)) {
                return shelf;
            }
        }
        return null;
    }

    public static boolean addBookToShelf(Context context, String userId, String shelfTitle, ShelfBook book) {
        List<Shelf> shelves = getShelves(context, userId);
        boolean added = false;
        for (Shelf shelf : shelves) {
            if (shelf.getTitle().equalsIgnoreCase(shelfTitle)) {
                int countBefore = shelf.getBookCount();
                shelf.addBook(book);
                added = shelf.getBookCount() > countBefore;
                break;
            }
        }
        saveShelves(context, userId, shelves);
        return added;
    }

    public static void deleteShelf(Context context, String userId, String shelfTitle) {
        List<Shelf> shelves = getShelves(context, userId);
        shelves.removeIf(shelf -> shelf.getTitle().equalsIgnoreCase(shelfTitle));
        saveShelves(context, userId, shelves);
    }

    public static boolean removeBookFromShelf(Context context, String userId, String shelfTitle, String bookTitle) {
        List<Shelf> shelves = getShelves(context, userId);
        boolean removed = false;
        for (Shelf shelf : shelves) {
            if (shelf.getTitle().equalsIgnoreCase(shelfTitle)) {
                removed = shelf.removeBook(bookTitle);
                break;
            }
        }
        saveShelves(context, userId, shelves);
        return removed;
    }

    public static boolean updateShelfDetails(Context context, String userId, String currentShelfTitle, String newShelfTitle, String newShelfDescription) {
        List<Shelf> shelves = getShelves(context, userId);
        for (int i = 0; i < shelves.size(); i++) {
            Shelf shelf = shelves.get(i);
            if (shelf.getTitle().equalsIgnoreCase(currentShelfTitle)) {
                for (int j = 0; j < shelves.size(); j++) {
                    if (i != j && shelves.get(j).getTitle().equalsIgnoreCase(newShelfTitle)) {
                        return false;
                    }
                }

                shelves.set(i, new Shelf(newShelfTitle, newShelfDescription, shelf.getBooks()));
                saveShelves(context, userId, shelves);
                return true;
            }
        }
        return false;
    }

    private static void saveShelves(Context context, String userId, List<Shelf> shelves) {
        if (userId == null || userId.isEmpty()) {
            return;
        }

        JSONArray shelfArray = new JSONArray();
        try {
            for (Shelf shelf : shelves) {
                JSONObject shelfObject = new JSONObject();
                shelfObject.put("title", shelf.getTitle());
                shelfObject.put("description", shelf.getDescription());

                JSONArray booksArray = new JSONArray();
                for (ShelfBook book : shelf.getBooks()) {
                    JSONObject bookObject = new JSONObject();
                    bookObject.put("title", book.getTitle());
                    bookObject.put("authors", book.getAuthors());
                    bookObject.put("thumbnailUrl", book.getThumbnailUrl());
                    bookObject.put("publishedDate", book.getPublishedDate());
                    bookObject.put("averageRating", book.getAverageRating());
                    booksArray.put(bookObject);
                }
                shelfObject.put("books", booksArray);
                shelfArray.put(shelfObject);
            }
        } catch (JSONException ignored) {
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PREFIX + userId, shelfArray.toString()).apply();
    }

    private static boolean isLegacyPlaceholderSet(List<Shelf> shelves) {
        if (shelves.size() != 3) {
            return false;
        }

        boolean matchesTitles =
                "Want to Read".equalsIgnoreCase(shelves.get(0).getTitle()) &&
                "Already Read".equalsIgnoreCase(shelves.get(1).getTitle()) &&
                "Favorites".equalsIgnoreCase(shelves.get(2).getTitle());

        if (!matchesTitles) {
            return false;
        }

        for (Shelf shelf : shelves) {
            if (shelf.getBookCount() > 0) {
                return false;
            }
        }

        return true;
    }
}
