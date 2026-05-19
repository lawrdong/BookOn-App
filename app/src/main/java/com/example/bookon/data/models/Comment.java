package com.example.bookon.data.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class Comment {
    @DocumentId
    private String id;

    @PropertyName("postId")
    private String postId;

    @PropertyName("userId")
    private String userId;

    @PropertyName("userName")
    private String userName;

    @PropertyName("text")
    private String text;

    @PropertyName("createdAt")
    private String createdAt;

    public Comment() {
    }

    public Comment(String postId, String userId, String userName, String text, String createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("postId")
    public String getPostId() { return postId; }
    @PropertyName("postId")
    public void setPostId(String postId) { this.postId = postId; }

    @PropertyName("userId")
    public String getUserId() { return userId; }
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("userName")
    public String getUserName() { return userName; }
    @PropertyName("userName")
    public void setUserName(String userName) { this.userName = userName; }

    @PropertyName("text")
    public String getText() { return text; }
    @PropertyName("text")
    public void setText(String text) { this.text = text; }

    @PropertyName("createdAt")
    public String getCreatedAt() { return createdAt; }
    @PropertyName("createdAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
