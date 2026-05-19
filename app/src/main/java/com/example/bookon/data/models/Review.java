package com.example.bookon.data.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Review {
    @DocumentId
    private String id;

    @PropertyName("userId")
    private String userId;

    @PropertyName("userName")
    private String userName;

    @PropertyName("userProfilePic")
    private String userProfilePic;
    
    @PropertyName("bookId")
    private String bookId;
    
    @PropertyName("bookTitle")
    private String bookTitle;
    
    @PropertyName("rating")
    private int rating;
    
    @PropertyName("reviewText")
    private String reviewText;
    
    @PropertyName("createdAt")
    private String createdAt;

    public Review() {
    }

    public Review(String userId, String userName, String userProfilePic, String bookId, String bookTitle, int rating, String reviewText) {
        this.userId = userId;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = String.valueOf(System.currentTimeMillis());
    }

    // Getters and Setters
    @PropertyName("userId")
    public String getUserId() { return userId; }
    
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("userName")
    public String getUserName() { return userName; }

    @PropertyName("userName")
    public void setUserName(String userName) { this.userName = userName; }

    @PropertyName("userProfilePic")
    public String getUserProfilePic() { return userProfilePic; }

    @PropertyName("userProfilePic")
    public void setUserProfilePic(String userProfilePic) { this.userProfilePic = userProfilePic; }

    @PropertyName("bookId")
    public String getBookId() { return bookId; }

    @PropertyName("bookId")
    public void setBookId(String bookId) { this.bookId = bookId; }

    @PropertyName("bookTitle")
    public String getBookTitle() { return bookTitle; }

    @PropertyName("bookTitle")
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    @PropertyName("rating")
    public int getRating() { return rating; }

    @PropertyName("rating")
    public void setRating(int rating) { this.rating = rating; }

    @PropertyName("reviewText")
    public String getReviewText() { return reviewText; }

    @PropertyName("reviewText")
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    @PropertyName("createdAt")
    public String getCreatedAt() { return createdAt; }

    @PropertyName("createdAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
