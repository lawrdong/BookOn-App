package com.example.bookon.data.models;

public class ShelfBook {

    private final String id;
    private final String title;
    private final String authors;
    private final String thumbnailUrl;
    private final String description;
    private final String publishedDate;
    private final double averageRating;

    public ShelfBook(String id, String title, String authors, String thumbnailUrl, String description, String publishedDate, double averageRating) {
        this.id = id != null ? id : "";
        this.title = title != null ? title : "";
        this.authors = authors != null ? authors : "Unknown Author";
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : "";
        this.description = description != null ? description : "";
        this.publishedDate = publishedDate != null ? publishedDate : "";
        this.averageRating = averageRating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public double getAverageRating() {
        return averageRating;
    }
}
