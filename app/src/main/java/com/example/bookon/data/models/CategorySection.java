package com.example.bookon.data.models;

import java.util.List;

public class CategorySection {
    private final String title;
    private final List<Book> books;

    public CategorySection(String title, List<Book> books) {
        this.title = title;
        this.books = books;
    }

    public String getTitle() {
        return title;
    }

    public List<Book> getBooks() {
        return books;
    }
}
