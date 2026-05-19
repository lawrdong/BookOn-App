package com.example.bookon.data.repositories;

import android.util.Log;

import com.example.bookon.data.api.GoogleBooksApi;
import com.example.bookon.data.api.GoogleBooksResponse;
import com.example.bookon.data.models.Book;
import com.example.bookon.data.models.Comment;
import com.example.bookon.data.models.Post;
import com.example.bookon.data.models.Review;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Repository class for handling book-related data operations.
 */
public class BookRepository {

    private static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/";
    private final GoogleBooksApi googleApi;
    private final FirebaseFirestore db;

    /**
     * Google Books API Key
     * You can ask me for it (Kevin) or you can make your own key if that's easier for you
     */
    private static final String API_KEY = "api_key_goes_here";

    // Queries used for random initial loading
    private final String[] randomQueries = {
        "mystery", "science fiction", "fantasy", "biography", "history", 
        "cooking", "travel", "art", "science", "business", "romance", 
        "thriller", "philosophy", "psychology", "technology", "nature"
    };

    public BookRepository() {
        // Initialize Retrofit for Google Books API
        Retrofit googleRetrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_BOOKS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googleApi = googleRetrofit.create(GoogleBooksApi.class);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Callback interface for asynchronous book data fetching.
     */
    public interface BookCallback {
        void onSuccess(List<Book> books);
        void onError(Throwable t);
    }

    /**
     * Callback interface for fetching reviews.
     */
    public interface ReviewCallback {
        void onSuccess(List<Review> reviews);
        void onError(Throwable t);
    }

    /**
     * Callback interface for fetching posts.
     */
    public interface PostCallback {
        void onSuccess(List<Post> posts);
        void onError(Throwable t);
    }

    /**
     * Callback interface for fetching comments.
     */
    public interface CommentCallback {
        void onSuccess(List<Comment> comments);
        void onError(Throwable t);
    }

    public String getRandomTrendingQuery() {
        return randomQueries[new Random().nextInt(randomQueries.length)];
    }

    public List<String> getRandomQueries(int count) {
        List<String> queries = new ArrayList<>(java.util.Arrays.asList(randomQueries));
        Collections.shuffle(queries);
        return queries.subList(0, Math.min(count, queries.size()));
    }

    public List<String> getAllRandomQueries() {
        return new ArrayList<>(java.util.Arrays.asList(randomQueries));
    }

    /**
     * Fetches a random set of books for the initial browse screen state.
     */
    public void getTrendingBooks(int startIndex, int maxResults, String orderBy, BookCallback callback) {
        getTrendingBooks(getRandomTrendingQuery(), startIndex, maxResults, orderBy, callback);
    }

    /**
     * Fetches a set of books for the trending state using a specific query.
     */
    public void getTrendingBooks(String query, int startIndex, int maxResults, String orderBy, BookCallback callback) {
        googleApi.searchBooks(query, API_KEY, startIndex, maxResults, orderBy).enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = mapResponseToBooks(response.body());
                    callback.onSuccess(books);
                } else {
                    callback.onError(new Exception("Failed to fetch books"));
                }
            }

            @Override
            public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * Searches for books based on a user-provided query.
     */
    public void searchBooks(String query, int startIndex, int maxResults, String orderBy, BookCallback callback) {
        googleApi.searchBooks(query, API_KEY, startIndex, maxResults, orderBy).enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = mapResponseToBooks(response.body());


                    if (!"newest".equals(orderBy)) {

                    }

                    callback.onSuccess(books);
                } else {
                    callback.onError(new Exception("Failed to search books"));
                }
            }

            @Override
            public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * Callback interface for simple actions (like posting).
     */
    public interface ActionCallback {
        void onResponse(boolean success, String message);
    }

    /**
     * Real-time listener for reviews on a specific book.
     */
    public com.google.firebase.firestore.ListenerRegistration listenToReviewsForBook(String bookId, ReviewCallback callback) {
        return db.collection("reviews")
                .whereEqualTo("bookId", bookId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value != null) {
                        List<Review> reviews = value.toObjects(Review.class);
                        reviews.sort((a, b) -> {
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return b.getCreatedAt().compareTo(a.getCreatedAt()); // Newest first
                        });
                        callback.onSuccess(reviews);
                    }
                });
    }

    /**
     * Fetches reviews for a specific book from Firestore.
     */
    public void getReviewsForBook(String bookId, ReviewCallback callback) {
        db.collection("reviews")
                .whereEqualTo("bookId", bookId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> reviews = queryDocumentSnapshots.toObjects(Review.class);
                    for (Review r : reviews) {
                        Log.d("BookRepository", "Fetched review with ID: " + r.getId());
                    }
                    callback.onSuccess(reviews);
                })
                .addOnFailureListener(e -> {
                    Log.e("BookRepository", "Error fetching reviews", e);
                    callback.onError(e);
                });
    }

    /**
     * Posts a new review to Firestore.
     */
    public void postReview(String userId, String userName, String userProfilePic, String bookId, String title, int rating, String text, ActionCallback callback) {
        Map<String, Object> review = new HashMap<>();
        review.put("userId", userId);
        review.put("userName", userName);
        review.put("userProfilePic", userProfilePic);
        review.put("bookId", bookId);
        review.put("bookTitle", title);
        review.put("rating", rating);
        review.put("reviewText", text);
        review.put("createdAt", String.valueOf(System.currentTimeMillis())); // Simple timestamp

        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> callback.onResponse(true, "Review posted!"))
                .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
    }

    /**
     * Deletes a review from Firestore.
     */
    public void deleteReview(String reviewId, ActionCallback callback) {
        Log.d("BookRepository", "Attempting to delete review with ID: " + reviewId);
        db.collection("reviews")
                .document(reviewId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("BookRepository", "Successfully deleted review: " + reviewId);
                    callback.onResponse(true, "Review deleted");
                })
                .addOnFailureListener(e -> {
                    Log.e("BookRepository", "Failed to delete review: " + reviewId, e);
                    callback.onResponse(false, e.getMessage());
                });
    }

    /**
     * Real-time listener for community posts.
     */
    public com.google.firebase.firestore.ListenerRegistration listenToCommunityPosts(PostCallback callback) {
        return db.collection("posts")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value != null) {
                        List<Post> posts = value.toObjects(Post.class);
                        posts.sort((a, b) -> {
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return b.getCreatedAt().compareTo(a.getCreatedAt()); // Newest first
                        });
                        callback.onSuccess(posts);
                    }
                });
    }

    /**
     * Real-time listener for comments on a specific post.
     */
    public com.google.firebase.firestore.ListenerRegistration listenToCommentsForPost(String postId, CommentCallback callback) {
        return db.collection("comments")
                .whereEqualTo("postId", postId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value != null) {
                        List<Comment> comments = value.toObjects(Comment.class);
                        comments.sort((a, b) -> {
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return a.getCreatedAt().compareTo(b.getCreatedAt()); // Oldest first
                        });
                        callback.onSuccess(comments);
                    }
                });
    }

    /**
     * Fetches all community posts from Firestore.
     */
    public void getCommunityPosts(PostCallback callback) {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = queryDocumentSnapshots.toObjects(Post.class);
                    // Sort manually in memory to avoid index requirements or missing field issues
                    posts.sort((a, b) -> {
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return b.getCreatedAt().compareTo(a.getCreatedAt()); // Newest first
                    });
                    callback.onSuccess(posts);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Publishes a new community post to Firestore.
     */
    public void publishPost(Post post, ActionCallback callback) {
        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> callback.onResponse(true, "Post published!"))
                .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
    }

    /**
     * Deletes a community post from Firestore.
     */
    public void deletePost(String postId, ActionCallback callback) {
        db.collection("posts")
                .document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onResponse(true, "Post deleted"))
                .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
    }

    /**
     * Toggles a like on a post for a specific user.
     */
    public void toggleLikePost(String postId, String userId, boolean currentlyLiked, ActionCallback callback) {
        if (currentlyLiked) {
            db.collection("posts").document(postId)
                    .update("likedBy", FieldValue.arrayRemove(userId))
                    .addOnSuccessListener(aVoid -> callback.onResponse(true, "Unliked"))
                    .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
        } else {
            db.collection("posts").document(postId)
                    .update("likedBy", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener(aVoid -> callback.onResponse(true, "Liked"))
                    .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
        }
    }

    /**
     * Deletes a comment from Firestore.
     */
    public void deleteComment(String commentId, ActionCallback callback) {
        db.collection("comments")
                .document(commentId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onResponse(true, "Comment deleted"))
                .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
    }

    /**
     * Fetches all comments for a specific post from Firestore.
     */
    public void getCommentsForPost(String postId, CommentCallback callback) {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = queryDocumentSnapshots.toObjects(Comment.class);
                    comments.sort((a, b) -> {
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return a.getCreatedAt().compareTo(b.getCreatedAt()); // Oldest first for comments
                    });
                    callback.onSuccess(comments);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Publishes a new comment to a post in Firestore.
     */
    public void publishComment(Comment comment, ActionCallback callback) {
        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> callback.onResponse(true, "Comment posted!"))
                .addOnFailureListener(e -> callback.onResponse(false, e.getMessage()));
    }

    /**
     * Helper to map the API response model to our internal Book domain model.
     */
    private List<Book> mapResponseToBooks(GoogleBooksResponse response) {
        List<Book> books = new ArrayList<>();
        if (response.getItems() != null) {
            for (GoogleBooksResponse.Item item : response.getItems()) {
                GoogleBooksResponse.VolumeInfo info = item.getVolumeInfo();
                String authors = (info.getAuthors() != null && !info.getAuthors().isEmpty()) 
                        ? String.join(", ", info.getAuthors()) : "Unknown Author";
                String thumbnail = (info.getImageLinks() != null)
                        ? info.getImageLinks().getThumbnail().replace("http://", "https://") : null;

                books.add(new Book(
                        item.getId(),
                        info.getTitle(),
                        authors,
                        info.getDescription(),
                        thumbnail,
                        info.getPreviewLink(),
                        info.getPublishedDate(),
                        info.getAverageRating(),
                        info.getRatingsCount(),
                        info.getCategories()
                ));
            }
        }
        return books;
    }
}
