package com.example.bookon.data.models;

public class Shelf {

    private String title;
    private String description;
    private int bookCount;

    public Shelf(String title, String description, int bookCount) {
        this.title = title;
        this.description = description;
        this.bookCount = bookCount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getBookCount() {
        return bookCount;
    }
}