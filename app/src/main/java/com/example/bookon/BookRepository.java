package com.example.bookon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/";
    private final GoogleBooksApi api;

    /**
     * Google Books API Key
     * You can ask me for it (Kevin) or you can make your own key if that's easier for you
     */
    private static final String API_KEY = "api key goes here";

    // Queries used for random initial loading
    private final String[] randomQueries = {
        "mystery", "science fiction", "fantasy", "biography", "history", 
        "cooking", "travel", "art", "science", "business", "romance", 
        "thriller", "philosophy", "psychology", "technology", "nature"
    };

    public BookRepository() {
        // Initialize Retrofit for API calls
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(GoogleBooksApi.class);
    }

    /**
     * Callback interface for asynchronous book data fetching.
     */
    public interface BookCallback {
        void onSuccess(List<Book> books);
        void onError(Throwable t);
    }

    /**
     * Fetches a random set of books for the initial browse screen state.
     */
    public void getTrendingBooks(int startIndex, int maxResults, BookCallback callback) {
        String randomQuery = randomQueries[new Random().nextInt(randomQueries.length)];
        
        api.searchBooks(randomQuery, API_KEY, startIndex, maxResults).enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = mapResponseToBooks(response.body());
                    // Shuffle for added variety
                    Collections.shuffle(books);
                    // Sort so books with ratings show up first by default
                    sortBooksByRatingDescending(books);
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
    public void searchBooks(String query, int startIndex, int maxResults, BookCallback callback) {
        api.searchBooks(query, API_KEY, startIndex, maxResults).enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = mapResponseToBooks(response.body());
                    // Sort so books with ratings show up first by default
                    sortBooksByRatingDescending(books);
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
     * Helper to sort books such that those with higher ratings appear first.
     */
    private void sortBooksByRatingDescending(List<Book> books) {
        Collections.sort(books, (b1, b2) -> {
            Double r1 = b1.getAverageRating();
            Double r2 = b2.getAverageRating();
            
            if (r1 != null && r2 == null) return -1;
            if (r1 == null && r2 != null) return 1;
            if (r1 != null && r2 != null) {
                return r2.compareTo(r1);
            }
            return 0;
        });
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
