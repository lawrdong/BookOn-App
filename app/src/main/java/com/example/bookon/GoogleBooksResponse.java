package com.example.bookon;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GoogleBooksResponse {
    @SerializedName("items")
    private List<Item> items;

    public List<Item> getItems() { return items; }

    public static class Item {
        @SerializedName("id")
        private String id;
        @SerializedName("volumeInfo")
        private VolumeInfo volumeInfo;

        public String getId() { return id; }
        public VolumeInfo getVolumeInfo() { return volumeInfo; }
    }

    public static class VolumeInfo {
        @SerializedName("title")
        private String title;
        @SerializedName("authors")
        private List<String> authors;
        @SerializedName("description")
        private String description;
        @SerializedName("imageLinks")
        private ImageLinks imageLinks;
        @SerializedName("previewLink")
        private String previewLink;
        @SerializedName("publishedDate")
        private String publishedDate;

        public String getTitle() { return title; }
        public List<String> getAuthors() { return authors; }
        public String getDescription() { return description; }
        public ImageLinks getImageLinks() { return imageLinks; }
        public String getPreviewLink() { return previewLink; }
        public String getPublishedDate() { return publishedDate; }
    }

    public static class ImageLinks {
        @SerializedName("thumbnail")
        private String thumbnail;

        public String getThumbnail() { return thumbnail; }
    }
}
