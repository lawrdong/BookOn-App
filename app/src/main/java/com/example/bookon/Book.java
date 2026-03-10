package com.example.bookon;

public class Book {
    private String id;
    private String title;
    private String authors;
    private String description;
    private String thumbnailUrl;
    private String previewLink;
    private String publishedDate;

    public Book(String id, String title, String authors, String description,
                String thumbnailUrl, String previewLink, String publishedDate) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.previewLink = previewLink;
        this.publishedDate = publishedDate;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getPreviewLink() { return previewLink; }
    public String getPublishedDate() { return publishedDate; }
}