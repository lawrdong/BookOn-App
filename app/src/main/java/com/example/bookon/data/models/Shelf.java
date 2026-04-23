package com.example.bookon.data.models;

import java.util.ArrayList;
import java.util.List;

public class Shelf {

    private final String title;
    private final String description;
    private final List<ShelfBook> books;

    public Shelf(String title, String description, int bookCount) {
        this(title, description, createPlaceholderBooks(bookCount));
    }

    public Shelf(String title, String description, List<ShelfBook> books) {
        this.title = title;
        this.description = description;
        this.books = books != null ? new ArrayList<>(books) : new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getBookCount() {
        return books.size();
    }

    public List<ShelfBook> getBooks() {
        return new ArrayList<>(books);
    }

    public boolean containsBook(String title) {
        if (title == null || title.isEmpty()) {
            return false;
        }

        for (ShelfBook book : books) {
            if (title.equalsIgnoreCase(book.getTitle())) {
                return true;
            }
        }
        return false;
    }

    public void addBook(ShelfBook book) {
        if (book != null && !book.getTitle().isEmpty() && !containsBook(book.getTitle())) {
            books.add(book);
        }
    }

    public boolean removeBook(String title) {
        for (int i = 0; i < books.size(); i++) {
            if (title != null && title.equalsIgnoreCase(books.get(i).getTitle())) {
                books.remove(i);
                return true;
            }
        }
        return false;
    }

    private static List<ShelfBook> createPlaceholderBooks(int bookCount) {
        List<ShelfBook> placeholders = new ArrayList<>();
        for (int i = 0; i < bookCount; i++) {
            placeholders.add(new ShelfBook("Book " + (i + 1), "Unknown Author", "", "", 0.0));
        }
        return placeholders;
    }
}
