package com.example.aolmobprog;

import com.google.firebase.firestore.GeoPoint; // <-- Tambahkan import ini
import com.google.firebase.firestore.Exclude;  // <-- Tambahkan import ini

public class TodoContent {
    private String title;private String description;
    private GeoPoint location;

    public TodoContent() {}

    public TodoContent(String title, String description, GeoPoint location) {
        this.title = title;
        this.description = description;
        this.location = location;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public GeoPoint getLocation() { return location; }

    @Exclude
    public Double getLatitude() {
        if (location != null) {
            return location.getLatitude();
        }
        return null;
    }

    @Exclude
    public Double getLongitude() {
        if (location != null) {
            return location.getLongitude();
        }
        return null;
    }
}
