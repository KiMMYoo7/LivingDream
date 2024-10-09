package com.example.livingdream;

public class Article {
    private String title;
    private String url;
    private String details;
    private int imageResId;

    // Constructor
    public Article(String title, String url, String details, int imageResId) {
        this.title = title;
        this.url = url;
        this.details = details;
        this.imageResId = imageResId;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResId() {
        return imageResId;
    }
}
