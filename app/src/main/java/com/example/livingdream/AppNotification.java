package com.example.livingdream;

public class AppNotification {
    private String title;
    private String message;
    private float bmi;
    private long timestamp; // New field for timestamp

    // Constructor
    public AppNotification(String title, String message, float bmi, long timestamp) {
        this.title = title;
        this.message = message;
        this.bmi = bmi;
        this.timestamp = timestamp;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public float getBmi() {
        return bmi;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
