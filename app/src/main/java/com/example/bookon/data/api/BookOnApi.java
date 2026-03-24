package com.example.bookon.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BookOnApi {

    String BASE_URL = "http://10.0.2.2:3000/api/";

    // ✅ Removed login and register endpoints — Firebase handles this now
    @POST("reviews")
    Call<SimpleResponse> postReview(@Body ReviewRequest request);

    class ReviewRequest {
        public String user_id;   // ✅ Changed from int to String (Firebase UIDs are Strings)
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