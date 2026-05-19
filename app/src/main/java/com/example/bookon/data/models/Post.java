package com.example.bookon.data.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Post {
    @DocumentId
    private String id;

    @PropertyName("userId")
    private String userId;

    @PropertyName("userName")
    private String userName;

    @PropertyName("title")
    private String title;

    @PropertyName("body")
    private String body;

    @PropertyName("createdAt")
    private String createdAt;

    @PropertyName("bookId")
    private String bookId;

    @PropertyName("bookTitle")
    private String bookTitle;

    @PropertyName("bookAuthor")
    private String bookAuthor;

    @PropertyName("bookThumbnail")
    private String bookThumbnail;

    @PropertyName("bookDescription")
    private String bookDescription;

    @PropertyName("bookPublishedDate")
    private String bookPublishedDate;

    @PropertyName("bookAverageRating")
    private double bookAverageRating;

    @PropertyName("likedBy")
    private List<String> likedBy = new ArrayList<>();

    public Post() {
    }

    public Post(String userId, String userName, String title, String body, String createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("userId")
    public String getUserId() { return userId; }
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("userName")
    public String getUserName() { return userName; }
    @PropertyName("userName")
    public void setUserName(String userName) { this.userName = userName; }

    @PropertyName("title")
    public String getTitle() { return title; }
    @PropertyName("title")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("body")
    public String getBody() { return body; }
    @PropertyName("body")
    public void setBody(String body) { this.body = body; }

    @PropertyName("createdAt")
    public String getCreatedAt() { return createdAt; }
    @PropertyName("createdAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @PropertyName("bookId")
    public String getBookId() { return bookId; }
    @PropertyName("bookId")
    public void setBookId(String bookId) { this.bookId = bookId; }

    @PropertyName("bookTitle")
    public String getBookTitle() { return bookTitle; }
    @PropertyName("bookTitle")
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    @PropertyName("bookAuthor")
    public String getBookAuthor() { return bookAuthor; }
    @PropertyName("bookAuthor")
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    @PropertyName("bookThumbnail")
    public String getBookThumbnail() { return bookThumbnail; }
    @PropertyName("bookThumbnail")
    public void setBookThumbnail(String bookThumbnail) { this.bookThumbnail = bookThumbnail; }

    @PropertyName("bookDescription")
    public String getBookDescription() { return bookDescription; }
    @PropertyName("bookDescription")
    public void setBookDescription(String bookDescription) { this.bookDescription = bookDescription; }

    @PropertyName("bookPublishedDate")
    public String getBookPublishedDate() { return bookPublishedDate; }
    @PropertyName("bookPublishedDate")
    public void setBookPublishedDate(String bookPublishedDate) { this.bookPublishedDate = bookPublishedDate; }

    @PropertyName("bookAverageRating")
    public double getBookAverageRating() { return bookAverageRating; }
    @PropertyName("bookAverageRating")
    public void setBookAverageRating(double bookAverageRating) { this.bookAverageRating = bookAverageRating; }

    @PropertyName("likedBy")
    public List<String> getLikedBy() { return likedBy; }
    @PropertyName("likedBy")
    public void setLikedBy(List<String> likedBy) { this.likedBy = likedBy; }
}
