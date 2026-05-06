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
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookon.data.models.CategorySection;
import com.example.bookon.utils.CategorySectionAdapter;
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
    private TextView tvBrowsePickerBanner;
    private boolean selectForPost;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private CategorySectionAdapter categorySectionAdapter;
    private ProgressBar progressBar;
    private EditText etSearch;
    private Button btnSearch;
    private ImageButton btnFilter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> filteredBookList = new ArrayList<>();
    private List<CategorySection> categorySections = new ArrayList<>();

    private int startIndex = 0;
    private final int maxResults = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isInCategoryFallbackMode = false;
    private boolean isShowingCategories = false;
    private String currentQuery = "";

    private String selectedCategory = "All Categories";
    private String selectedSort = "relevance";
    private String activeTrendingQuery = null;

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
        tvBrowsePickerBanner = findViewById(R.id.tvBrowsePickerBanner);
        progressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnFilter = findViewById(R.id.btnFilter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        selectForPost = getIntent().getBooleanExtra("selectForPost", false);

        if (tvBrowsePickerBanner != null) {
            tvBrowsePickerBanner.setVisibility(selectForPost ? View.VISIBLE : View.GONE);
            String pickerBannerText = getIntent().getStringExtra("pickerBannerText");
            if (pickerBannerText != null && !pickerBannerText.isEmpty()) {
                tvBrowsePickerBanner.setText(pickerBannerText);
            }
        }

        // Navigation
        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }

        if (tabCommunity != null) {
            tabCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseActivity.this, CommunityActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

        // Swipe Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            resetSearchAndReload();
        });

        // Tab click behavior
        if (tabBrowse != null) {
            tabBrowse.setOnClickListener(v -> {
                if (recyclerView != null) {
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewBooks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        BookAdapter.OnBookClickListener clickListener = null;
        if (selectForPost) {
            clickListener = book -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", book.getId());
                resultIntent.putExtra("title", book.getTitle());
                resultIntent.putExtra("authors", book.getAuthors());
                resultIntent.putExtra("thumbnailUrl", book.getThumbnailUrl());
                resultIntent.putExtra("publishedDate", book.getPublishedDate());
                resultIntent.putExtra("averageRating", book.getAverageRating() != null ? book.getAverageRating() : 0.0);
                setResult(RESULT_OK, resultIntent);
                finish();
            };
        }

        adapter = new BookAdapter(filteredBookList, clickListener);
        categorySectionAdapter = new CategorySectionAdapter(categorySections, clickListener);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage && !isShowingCategories) {
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
        if (selectedSort.equals("newest")) {
            rgSort.check(R.id.rbNewest);
        } else {
            rgSort.check(R.id.rbRelevance);
        }

        Button btnCategory = dialogView.findViewById(R.id.btnCategory);
        btnCategory.setText(selectedCategory);
        btnCategory.setOnClickListener(v -> showCategorySelector(btnCategory));

        builder.setTitle(R.string.filter_books);
        builder.setPositiveButton(R.string.apply, (dialog, which) -> {
            String newCategory = btnCategory.getText().toString();
            String newSort = (rgSort.getCheckedRadioButtonId() == R.id.rbNewest) ? "newest" : "relevance";

            if (!newCategory.equals(selectedCategory) || !newSort.equals(selectedSort)) {
                selectedCategory = newCategory;
                selectedSort = newSort;
                resetSearchAndReload();
            }
        });
        builder.setNeutralButton(R.string.clear_filters, (dialog, which) -> {
            if (!selectedCategory.equals("All Categories") || !selectedSort.equals("relevance")) {
                selectedCategory = "All Categories";
                selectedSort = "relevance";
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
        categorySections.clear();
        adapter.notifyDataSetChanged();
        categorySectionAdapter.notifyDataSetChanged();
        loadBooks();
    }

    private void loadBooks() {
        if (isLoading) return;

        // If no search and no filter, show categories (Netflix style)
        if (currentQuery.isEmpty() && selectedCategory.equals("All Categories")) {
            if (!categorySections.isEmpty()) return; // Already loaded initial categories
            loadCategorySections();
            return;
        }

        if (isLastPage) return;

        isShowingCategories = false;
        recyclerView.setAdapter(adapter);
        isLoading = true;
        if (progressBar != null && (swipeRefreshLayout == null || !swipeRefreshLayout.isRefreshing())) {
            progressBar.setVisibility(View.VISIBLE);
        }

        BookRepository repo = new BookRepository();
        BookRepository.BookCallback callback = new BookRepository.BookCallback() {
            @Override
            public void onSuccess(List<Book> books) {
                isLoading = false;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                if (books.isEmpty()) {
                    if (!selectedCategory.equals("All Categories") && !isInCategoryFallbackMode && startIndex == 0) {
                        isInCategoryFallbackMode = true;
                        searchByCategoryKeyword(repo, this);
                    } else {
                        isLastPage = true;
                    }
                } else {
                    int startPosition = filteredBookList.size();

                    // Filter out books from 1984 or older if there is an empty search query or a category filter is active
                    List<Book> validBooks = new ArrayList<>();
                    boolean shouldFilterByDate = currentQuery.isEmpty();
                    
                    if (shouldFilterByDate) {
                        for (Book book : books) {
                            String date = book.getPublishedDate();
                            if (date != null && !date.isEmpty()) {
                                try {
                                    // Google Books dates can be YYYY-MM-DD or just YYYY
                                    int year = Integer.parseInt(date.substring(0, 4));
                                    if (year > 1989) {
                                        validBooks.add(book);
                                    }
                                } catch (Exception e) {
                                    validBooks.add(book);
                                }
                            } else {
                                // If no date, keep it
                                validBooks.add(book);
                            }
                        }
                    } else {
                        validBooks.addAll(books);
                    }

                    if (validBooks.isEmpty() && !isLastPage) {
                        startIndex += maxResults;
                        isLoading = false;
                        loadBooks();
                        return;
                    }

                    bookList.addAll(validBooks);
                    filteredBookList.addAll(validBooks);

                    adapter.notifyItemRangeInserted(startPosition, validBooks.size());

                    startIndex += maxResults;

                    if (books.size() < maxResults) {
                        isLastPage = true;
                    }

                    if (filteredBookList.size() < 5 && !isLastPage) {
                        loadBooks();
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                isLoading = false;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
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
            if (activeTrendingQuery == null) {
                activeTrendingQuery = repo.getRandomTrendingQuery();
            }
            repo.getTrendingBooks(activeTrendingQuery, startIndex, maxResults, selectedSort, callback);
        } else {
            activeTrendingQuery = null;
            repo.searchBooks(apiQuery, startIndex, maxResults, selectedSort, callback);
        }
    }

    private void loadCategorySections() {
        isShowingCategories = true;
        recyclerView.setAdapter(categorySectionAdapter);
        categorySections.clear();
        categorySectionAdapter.notifyDataSetChanged();
        
        isLoading = true;
        if (progressBar != null && (swipeRefreshLayout == null || !swipeRefreshLayout.isRefreshing())) {
            progressBar.setVisibility(View.VISIBLE);
        }

        BookRepository repo = new BookRepository();
        // Try all available categories to ensure we fill up to 6 sections
        List<String> availableCategories = repo.getAllRandomQueries();
        Collections.shuffle(availableCategories);
        
        final int TARGET_COUNT = 6;
        final int[] categoriesFinished = {0};
        final Random random = new Random();

        for (String category : availableCategories) {
            // Smaller random offset to increase chances of finding enough books
            int randomStart = random.nextInt(40);
            
            fetchModernBooksForCategory(repo, category, randomStart, new ArrayList<>(), new BookRepository.BookCallback() {
                @Override
                public void onSuccess(List<Book> modernBooks) {
                    runOnUiThread(() -> {
                        // Only add if we haven't reached our desired count and the list isn't empty
                        if (!modernBooks.isEmpty() && categorySections.size() < TARGET_COUNT) {
                            // If we have at least 5 books, it's worth showing as a row
                            if (modernBooks.size() >= 5) {
                                Collections.shuffle(modernBooks);
                                categorySections.add(new CategorySection(category.toUpperCase(), modernBooks));
                                categorySectionAdapter.notifyItemInserted(categorySections.size() - 1);
                            }
                        }
                        
                        categoriesFinished[0]++;
                        if (categorySections.size() >= TARGET_COUNT || categoriesFinished[0] == availableCategories.size()) {
                            isLoading = false;
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onError(Throwable t) {
                    runOnUiThread(() -> {
                        categoriesFinished[0]++;
                        if (categorySections.size() >= TARGET_COUNT || categoriesFinished[0] == availableCategories.size()) {
                            isLoading = false;
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }

    private void fetchModernBooksForCategory(BookRepository repo, String category, int start, List<Book> accumulated, BookRepository.BookCallback finalCallback) {
        String queryWithDate = category + " after:1987";
        repo.getTrendingBooks(queryWithDate, start, 40, "relevance", new BookRepository.BookCallback() {
            @Override
            public void onSuccess(List<Book> books) {
                if (books.isEmpty()) {
                    finalCallback.onSuccess(accumulated);
                    return;
                }

                for (Book book : books) {
                    // Double check date even with API filter
                    String date = book.getPublishedDate();
                    boolean isModern = true;
                    if (date != null && date.length() >= 4) {
                        try {
                            int year = Integer.parseInt(date.substring(0, 4));
                            if (year < 1988) isModern = false;
                        } catch (NumberFormatException ignored) {}
                    }

                    if (isModern) {
                        // Filter out unknown authors
                        boolean hasAuthor = book.getAuthors() != null && !book.getAuthors().equalsIgnoreCase("Unknown Author");
                        
                        if (hasAuthor) {
                            // Check for duplicates
                            boolean exists = false;
                            for (Book b : accumulated) {
                                if (b.getId().equals(book.getId())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                accumulated.add(book);
                            }
                        }
                    }
                    if (accumulated.size() >= 20) break;
                }

                if (accumulated.size() < 20 && !books.isEmpty() && start < 400) {
                    fetchModernBooksForCategory(repo, category, start + 40, accumulated, finalCallback);
                } else {
                    finalCallback.onSuccess(accumulated);
                }
            }

            @Override
            public void onError(Throwable t) {
                // If error, return what we have so far
                finalCallback.onSuccess(accumulated);
            }
        });
    }

    private void searchByCategoryKeyword(BookRepository repo, BookRepository.BookCallback callback) {
        String fallbackQuery = currentQuery.isEmpty() ? selectedCategory : currentQuery + " " + selectedCategory;
        repo.searchBooks(fallbackQuery, startIndex, maxResults, selectedSort, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
    }
}
