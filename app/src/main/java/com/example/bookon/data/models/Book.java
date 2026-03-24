package com.example.bookon.data.models;

import java.util.List;

public class Book {
    private String id;
    private String title;
    private String authors;
    private String description;
    private String thumbnailUrl;
    private String previewLink;
    private String publishedDate;
    private Double averageRating;
    private Integer ratingsCount;
    private List<String> categories;

    public Book(String id, String title, String authors, String description,
                String thumbnailUrl, String previewLink, String publishedDate,
                Double averageRating, Integer ratingsCount, List<String> categories) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.previewLink = previewLink;
        this.publishedDate = publishedDate;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.categories = categories;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getPreviewLink() { return previewLink; }
    public String getPublishedDate() { return publishedDate; }
    public Double getAverageRating() { return averageRating; }
    public Integer getRatingsCount() { return ratingsCount; }
    public List<String> getCategories() { return categories; }
}
