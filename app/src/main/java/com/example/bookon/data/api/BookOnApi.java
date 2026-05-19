package com.example.bookon.data.api;

import com.example.bookon.data.models.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BookOnApi {

    String BASE_URL = "http://10.0.2.2:3000/api/";

    @POST("reviews")
    Call<SimpleResponse> postReview(@Body ReviewRequest request);

    @GET("reviews")
    Call<List<Review>> getReviews(@Query("bookId") String bookId);

    interface SimpleResponseCallback {
        void onResponse(boolean success, String message);
    }

    class ReviewRequest {
        public String user_id;
        public String book_id;
        public String book_title;
        public int rating;
        public String review_text;

        public ReviewRequest(String user_id, String book_id, String book_title, int rating, String review_text) {
            this.user_id = user_id;
            this.book_id = book_id;
            this.book_title = book_title;
            this.rating = rating;
            this.review_text = review_text;
        }
    }

    class SimpleResponse {
        public boolean success;
        public String message;
    }
}