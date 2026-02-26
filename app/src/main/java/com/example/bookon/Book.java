package com.example.bookon;

public class Book {
    private final String id;
    private final String title;
    private final String authors;
    private final String image;
    private final String description;

    public Book(String id, String title, String authors, String thumbnailUrl, String description) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.image = thumbnailUrl;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return authors; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
}