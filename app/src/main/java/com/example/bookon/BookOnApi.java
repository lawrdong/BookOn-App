package com.example.bookon;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BookOnApi {
    
    String BASE_URL = "http://10.0.2.2:3000/api/";

    @POST("users/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("users/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("reviews")
    Call<SimpleResponse> postReview(@Body ReviewRequest request);
    
    // Models
    class LoginRequest {
        String email;
        String password;
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class RegisterRequest {
        String username;
        String email;
        String password;
        public RegisterRequest(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    class ReviewRequest {
        int user_id;
        String book_id;
        String book_title;
        int rating;
        String review_text;

        public ReviewRequest(int user_id, String book_id, String book_title, int rating, String review_text) {
            this.user_id = user_id;
            this.book_id = book_id;
            this.book_title = book_title;
            this.rating = rating;
            this.review_text = review_text;
        }
    }

    class AuthResponse {
        boolean success;
        String message;
        User user;
    }

    class SimpleResponse {
        boolean success;
        String message;
    }

    class User {
        int id;
        String username;
        String email;
    }
}
