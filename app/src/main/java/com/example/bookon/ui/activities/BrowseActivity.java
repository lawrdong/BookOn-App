package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.utils.AuthManager;
import com.example.bookon.utils.BookAdapter;
import com.example.bookon.data.repositories.BookRepository;
import com.example.bookon.R;
import com.example.bookon.data.models.Book;

/**
 * Activity for browsing and searching books with filtering and sorting options.
 */
public class BrowseActivity extends AppCompatActivity {

    private TextView tabLogin;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private ProgressBar progressBar;
    private EditText etSearch;
    private Button btnSearch;
    private ImageButton btnFilter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> filteredBookList = new ArrayList<>();

    private int startIndex = 0;
    private final int maxResults = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isInCategoryFallbackMode = false;
    private String currentQuery = "";

    private int selectedSortId = R.id.rbRatingHighLow;
    private String selectedCategory = "All Categories";

    // Standard BISAC Top-Level Categories
    private final String[] bisacCategories = {
        "All Categories",
        "ANTIQUES & COLLECTIBLES",
        "ARCHITECTURE",
        "ART",
        "BIBLES",
        "BIOGRAPHY & AUTOBIOGRAPHY",
        "BODY, MIND & SPIRIT",
        "BUSINESS & ECONOMICS",
        "COMICS & GRAPHIC NOVELS",
        "COMPUTERS",
        "COOKING",
        "CRAFTS & HOBBIES",
        "DESIGN",
        "DRAMA",
        "EDUCATION",
        "FAMILY & RELATIONSHIPS",
        "FICTION",
        "FOREIGN LANGUAGE STUDY",
        "GAMES & ACTIVITIES",
        "GARDENING",
        "HEALTH & FITNESS",
        "HISTORY",
        "HOUSE & HOME",
        "HUMOR",
        "JUVENILE FICTION",
        "JUVENILE NONFICTION",
        "LANGUAGE ARTS & DISCIPLINES",
        "LAW",
        "LITERARY COLLECTIONS",
        "LITERARY CRITICISM",
        "MATHEMATICS",
        "MEDICAL",
        "MUSIC",
        "NATURE",
        "PERFORMING ARTS",
        "PETS",
        "PHILOSOPHY",
        "PHOTOGRAPHY",
        "POETRY",
        "POLITICAL SCIENCE",
        "PSYCHOLOGY",
        "REFERENCE",
        "RELIGION",
        "SCIENCE",
        "SELF-HELP",
        "SOCIAL SCIENCE",
        "SPORTS & RECREATION",
        "STUDY AIDS",
        "TECHNOLOGY & ENGINEERING",
        "TRANSPORTATION",
        "TRAVEL",
        "TRUE CRIME",
        "YOUNG ADULT FICTION",
        "YOUNG ADULT NONFICTION"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        TextView tabCommunity = findViewById(R.id.tabCommunity);
        tabLogin = findViewById(R.id.tabLogin);
        progressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);

        // Navigation
        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (tabCommunity != null) {
            tabCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, CommunityActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        // Search
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            performNewSearch(query);
        });

        // Filter
        btnFilter.setOnClickListener(v -> showFilterDialog());

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewBooks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(filteredBookList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    // Load more when we are near the end of the current list
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadBooks();
                    }
                }
            }
        });

        loadBooks(); // Initial load
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        RadioGroup rgSort = dialogView.findViewById(R.id.rgSort);
        rgSort.check(selectedSortId);

        Button btnCategory = dialogView.findViewById(R.id.btnCategory);
        btnCategory.setText(selectedCategory);
        btnCategory.setOnClickListener(v -> showCategorySelector(btnCategory));

        builder.setTitle(R.string.filter_books);
        builder.setPositiveButton(R.string.apply, (dialog, which) -> {
            int newSortId = rgSort.getCheckedRadioButtonId();
            String newCategory = btnCategory.getText().toString();

            if (!newCategory.equals(selectedCategory)) {
                selectedCategory = newCategory;
                selectedSortId = newSortId;
                resetSearchAndReload();
            } else {
                selectedSortId = newSortId;
                applyFiltersAndSort();
            }
        });
        builder.setNeutralButton(R.string.clear_filters, (dialog, which) -> {
            if (selectedSortId != R.id.rbRatingHighLow || !selectedCategory.equals("All Categories")) {
                selectedSortId = R.id.rbRatingHighLow;
                selectedCategory = "All Categories";
                resetSearchAndReload();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showCategorySelector(Button btnCategory) {
        List<String> categoriesList = new ArrayList<>(Arrays.asList(bisacCategories));

        for (Book book : bookList) {
            if (book.getCategories() != null) {
                for (String cat : book.getCategories()) {
                    String upperCat = cat.toUpperCase();
                    if (!categoriesList.contains(upperCat)) {
                        categoriesList.add(upperCat);
                    }
                }
            }
        }

        Collections.sort(categoriesList);

        categoriesList.remove("All Categories");
        categoriesList.add(0, "All Categories");

        String[] categoriesArray = categoriesList.toArray(new String[0]);
        int checkedItem = categoriesList.indexOf(btnCategory.getText().toString().toUpperCase());
        if (checkedItem == -1) checkedItem = 0;

        new AlertDialog.Builder(this)
                .setTitle(R.string.filter_by_category)
                .setSingleChoiceItems(categoriesArray, checkedItem, (dialog, which) -> {
                    btnCategory.setText(categoriesArray[which]);
                    dialog.dismiss();
                })
                .show();
    }

    private void applyFiltersAndSort() {
        List<Book> filtered = new ArrayList<>();

        for (Book book : bookList) {
            if (selectedCategory.equals("All Categories")) {
                filtered.add(book);
            } else if (book.getCategories() != null) {
                boolean match = false;
                String lowerCategory = selectedCategory.toLowerCase();
                for (String cat : book.getCategories()) {
                    if (cat.toLowerCase().contains(lowerCategory)) {
                        match = true;
                        break;
                    }
                }
                if (match) filtered.add(book);
            }
        }

        if (selectedSortId == R.id.rbRatingHighLow) {
            Collections.sort(filtered, (b1, b2) -> {
                Double r1 = b1.getAverageRating() != null ? b1.getAverageRating() : -1.0;
                Double r2 = b2.getAverageRating() != null ? b2.getAverageRating() : -1.0;
                return r2.compareTo(r1);
            });
        } else if (selectedSortId == R.id.rbRatingLowHigh) {
            Collections.sort(filtered, (b1, b2) -> {
                Double r1 = b1.getAverageRating() != null ? b1.getAverageRating() : 10.0;
                Double r2 = b2.getAverageRating() != null ? b2.getAverageRating() : 10.0;
                return r1.compareTo(r2);
            });
        } else if (selectedSortId == R.id.rbDateNewest) {
            Collections.sort(filtered, (b1, b2) -> {
                String d1 = b1.getPublishedDate() != null ? b1.getPublishedDate() : "";
                String d2 = b2.getPublishedDate() != null ? b2.getPublishedDate() : "";
                return d2.compareTo(d1);
            });
        } else if (selectedSortId == R.id.rbDateOldest) {
            Collections.sort(filtered, (b1, b2) -> {
                String d1 = b1.getPublishedDate() != null ? b1.getPublishedDate() : "9999";
                String d2 = b2.getPublishedDate() != null ? b2.getPublishedDate() : "9999";
                return d1.compareTo(d2);
            });
        }

        filteredBookList.clear();
        filteredBookList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    private void performNewSearch(String query) {
        currentQuery = query;
        resetSearchAndReload();
    }

    private void resetSearchAndReload() {
        startIndex = 0;
        isLastPage = false;
        isInCategoryFallbackMode = false;
        bookList.clear();
        filteredBookList.clear();
        adapter.notifyDataSetChanged();
        loadBooks();
    }

    private void loadBooks() {
        if (isLoading || isLastPage) return;

        isLoading = true;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        BookRepository repo = new BookRepository();
        BookRepository.BookCallback callback = new BookRepository.BookCallback() {
            @Override
            public void onSuccess(List<Book> books) {
                isLoading = false;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (books.isEmpty()) {
                    if (!selectedCategory.equals("All Categories") && !isInCategoryFallbackMode && startIndex == 0) {
                        isInCategoryFallbackMode = true;
                        searchByCategoryKeyword(repo, this);
                    } else {
                        isLastPage = true;
                    }
                } else {
                    bookList.addAll(books);
                    applyFiltersAndSort();
                    startIndex += maxResults;

                    // If we received fewer books than requested, it might be the end of results
                    if (books.size() < maxResults) {
                        isLastPage = true;
                    }

                    // If filtering resulted in very few items visible, try to load more immediately
                    if (filteredBookList.size() < 5 && !isLastPage) {
                        loadBooks();
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                isLoading = false;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(BrowseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        if (isInCategoryFallbackMode) {
            searchByCategoryKeyword(repo, callback);
            return;
        }

        String apiQuery = currentQuery;
        if (!selectedCategory.equals("All Categories")) {
            String categoryPart = "subject:\"" + selectedCategory + "\"";
            apiQuery = apiQuery.isEmpty() ? categoryPart : apiQuery + " " + categoryPart;
        }

        if (apiQuery.isEmpty()) {
            repo.getTrendingBooks(startIndex, maxResults, callback);
        } else {
            repo.searchBooks(apiQuery, startIndex, maxResults, callback);
        }
    }

    private void searchByCategoryKeyword(BookRepository repo, BookRepository.BookCallback callback) {
        String fallbackQuery = currentQuery.isEmpty() ? selectedCategory : currentQuery + " " + selectedCategory;
        repo.searchBooks(fallbackQuery, startIndex, maxResults, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
