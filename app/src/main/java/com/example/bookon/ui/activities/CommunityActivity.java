package com.example.bookon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.bookon.R;
import com.example.bookon.data.models.Comment;
import com.example.bookon.data.models.Post;
import com.example.bookon.data.repositories.BookRepository;
import com.example.bookon.utils.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommunityActivity extends AppCompatActivity {

    private TextView tabLogin;
    private LinearLayout layoutPostsContainer;
    private SwipeRefreshLayout swipeRefresh;
    private final List<Post> postItems = new ArrayList<>();
    private BookRepository repository;
    private ListenerRegistration postsListener;
    private final Map<String, ListenerRegistration> commentListeners = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        TextView tabHome = findViewById(R.id.tabHome);
        TextView tabBrowse = findViewById(R.id.tabBrowse);
        tabLogin = findViewById(R.id.tabLogin);
        Button btnCommunityPost = findViewById(R.id.btnCommunityPost);
        layoutPostsContainer = findViewById(R.id.layoutPostsContainer);
        swipeRefresh = findViewById(R.id.swipeRefreshCommunity);

        repository = new BookRepository();

        swipeRefresh.setOnRefreshListener(this::fetchPosts);

        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        tabBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityActivity.this, BrowseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        tabLogin.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        btnCommunityPost.setOnClickListener(v -> {
            if (AuthManager.isLoggedIn()) {
                startActivity(new Intent(this, CreatePostActivity.class));
            } else {
                Toast.makeText(this, "Login to create a post", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        startListeningToPosts();
    }

    private void startListeningToPosts() {
        if (postsListener != null) postsListener.remove();
        
        postsListener = repository.listenToCommunityPosts(new BookRepository.PostCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                swipeRefresh.setRefreshing(false);
                postItems.clear();
                postItems.addAll(posts);
                renderPosts();
            }

            @Override
            public void onError(Throwable t) {
                Log.e("CommunityActivity", "Error listening to posts", t);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void fetchPosts() {
        startListeningToPosts();
    }

    private void renderPosts() {
        Log.d("CommunityActivity", "Rendering " + postItems.size() + " posts");
        layoutPostsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Post post : postItems) {
            View postView = inflater.inflate(R.layout.item_post, layoutPostsContainer, false);
            TextView tvPostAvatar = postView.findViewById(R.id.tvPostAvatar);
            TextView tvPostUser = postView.findViewById(R.id.tvPostUser);
            TextView tvPostTime = postView.findViewById(R.id.tvPostTime);
            TextView tvPostTitle = postView.findViewById(R.id.tvPostTitle);
            TextView tvPostBody = postView.findViewById(R.id.tvPostBody);
            ImageView btnDeletePost = postView.findViewById(R.id.btnDeletePost);

            LinearLayout layoutLinkedBook = postView.findViewById(R.id.layoutPostLinkedBook);
            ImageView ivBookCover = postView.findViewById(R.id.ivPostBookCover);
            TextView tvBookTitle = postView.findViewById(R.id.tvPostBookTitle);
            TextView tvBookAuthor = postView.findViewById(R.id.tvPostBookAuthor);

            String userName = post.getUserName() != null ? post.getUserName() : "Unknown";
            tvPostUser.setText(userName);
            tvPostAvatar.setText(userName.substring(0, 1).toUpperCase());
            tvPostTime.setText(formatDate(post.getCreatedAt()));
            tvPostTitle.setText(post.getTitle());
            tvPostBody.setText(post.getBody());

            TextView btnLikePost = postView.findViewById(R.id.btnLikePost);
            TextView btnComment = postView.findViewById(R.id.btnComment);
            LinearLayout layoutCommentSection = postView.findViewById(R.id.layoutCommentSection);
            LinearLayout layoutCommentsList = postView.findViewById(R.id.layoutCommentsList);
            EditText etCommentInput = postView.findViewById(R.id.etCommentInput);
            TextView btnPostComment = postView.findViewById(R.id.btnPostComment);

            List<String> likedBy = post.getLikedBy() != null ? post.getLikedBy() : new ArrayList<>();
            boolean isLiked = AuthManager.isLoggedIn() && likedBy.contains(AuthManager.getUserId());
            int likeCount = likedBy.size();

            if (isLiked) {
                btnLikePost.setText("❤️ " + likeCount);
                btnLikePost.setTextColor(ContextCompat.getColor(this, R.color.accent_rose));
            } else {
                btnLikePost.setText("♡ " + (likeCount > 0 ? likeCount : "Like"));
                btnLikePost.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }

            btnLikePost.setOnClickListener(v -> {
                if (!AuthManager.isLoggedIn()) {
                    Toast.makeText(this, "Login to like posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                repository.toggleLikePost(post.getId(), AuthManager.getUserId(), isLiked, (success, message) -> {
                    if (!success) {
                        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            btnComment.setOnClickListener(v -> {
                if (layoutCommentSection.getVisibility() == View.VISIBLE) {
                    layoutCommentSection.setVisibility(View.GONE);
                    stopListeningToComments(post.getId());
                } else {
                    layoutCommentSection.setVisibility(View.VISIBLE);
                    startListeningToComments(post.getId(), layoutCommentsList);
                }
            });

            btnPostComment.setOnClickListener(v -> {
                if (!AuthManager.isLoggedIn()) {
                    Toast.makeText(this, "Login to reply", Toast.LENGTH_SHORT).show();
                    return;
                }
                String commentText = etCommentInput.getText().toString().trim();
                if (commentText.isEmpty()) return;

                postComment(post.getId(), commentText, etCommentInput, layoutCommentsList);
            });

            if (post.getBookId() != null) {
                layoutLinkedBook.setVisibility(View.VISIBLE);
                tvBookTitle.setText(post.getBookTitle());
                tvBookAuthor.setText(post.getBookAuthor());
                if (post.getBookThumbnail() != null) {
                    Glide.with(this).load(post.getBookThumbnail()).into(ivBookCover);
                }

                layoutLinkedBook.setOnClickListener(v -> {
                    Intent intent = new Intent(this, BookDetailActivity.class);
                    intent.putExtra("id", post.getBookId());
                    intent.putExtra("title", post.getBookTitle());
                    intent.putExtra("authors", post.getBookAuthor());
                    intent.putExtra("thumbnailUrl", post.getBookThumbnail());
                    intent.putExtra("description", post.getBookDescription());
                    intent.putExtra("publishedDate", post.getBookPublishedDate());
                    intent.putExtra("averageRating", post.getBookAverageRating());
                    startActivity(intent);
                });
            }

            if (AuthManager.isLoggedIn() && AuthManager.getUserId().equals(post.getUserId())) {
                btnDeletePost.setVisibility(View.VISIBLE);
                btnDeletePost.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Post")
                            .setMessage("Are you sure you want to delete this post?")
                            .setPositiveButton("Delete", (dialog, which) -> deletePost(post))
                            .setNegativeButton("Cancel", null)
                            .show();
                });
            }

            layoutPostsContainer.addView(postView);
        }
    }

    private void deletePost(Post post) {
        postItems.remove(post);
        renderPosts();
        repository.deletePost(post.getId(), (success, message) -> {
            if (!success) {
                Toast.makeText(this, "Failed to delete: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startListeningToComments(String postId, LinearLayout container) {
        stopListeningToComments(postId); // Clears existing first

        ListenerRegistration listener = repository.listenToCommentsForPost(postId, new BookRepository.CommentCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                container.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(CommunityActivity.this);
                for (Comment comment : comments) {
                    View commentView = inflater.inflate(R.layout.item_comment, container, false);
                    TextView tvAvatar = commentView.findViewById(R.id.tvCommentAvatar);
                    TextView tvUser = commentView.findViewById(R.id.tvCommentUser);
                    TextView tvText = commentView.findViewById(R.id.tvCommentText);
                    ImageView btnDeleteComment = commentView.findViewById(R.id.btnDeleteComment);

                    String name = comment.getUserName() != null ? comment.getUserName() : "User";
                    tvUser.setText(name);
                    tvAvatar.setText(name.substring(0, 1).toUpperCase());
                    tvText.setText(comment.getText());

                    if (AuthManager.isLoggedIn() && AuthManager.getUserId().equals(comment.getUserId())) {
                        btnDeleteComment.setVisibility(View.VISIBLE);
                        btnDeleteComment.setOnClickListener(v -> {
                            new AlertDialog.Builder(CommunityActivity.this)
                                    .setTitle("Delete Comment")
                                    .setMessage("Are you sure you want to delete this reply?")
                                    .setPositiveButton("Delete", (dialog, which) -> deleteComment(comment))
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        });
                    }

                    container.addView(commentView);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("CommunityActivity", "Error loading comments", t);
            }
        });

        commentListeners.put(postId, listener);
    }

    private void stopListeningToComments(String postId) {
        ListenerRegistration listener = commentListeners.remove(postId);
        if (listener != null) listener.remove();
    }

    private void deleteComment(Comment comment) {
        if (comment.getId() == null) return;
        repository.deleteComment(comment.getId(), (success, message) -> {
            if (!success) {
                Toast.makeText(this, "Failed to delete comment: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment(String postId, String text, EditText inputField, LinearLayout container) {
        String userId = AuthManager.getUserId();
        String userName = "Reader";
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            userName = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        }

        Comment comment = new Comment(postId, userId, userName, text, String.valueOf(System.currentTimeMillis()));
        repository.publishComment(comment, (success, message) -> {
            if (success) {
                inputField.setText("");
            } else {
                Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postsListener != null) postsListener.remove();
        for (ListenerRegistration listener : commentListeners.values()) {
            listener.remove();
        }
        commentListeners.clear();
    }

    private String formatDate(String timestampStr) {
        if (timestampStr == null) return "Recently";
        try {
            long time = Long.parseLong(timestampStr);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(time));
        } catch (NumberFormatException e) {
            return "Recently";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLogin != null) {
            tabLogin.setText(AuthManager.isLoggedIn() ? "Account" : "Login");
        }
        fetchPosts();
    }
}
