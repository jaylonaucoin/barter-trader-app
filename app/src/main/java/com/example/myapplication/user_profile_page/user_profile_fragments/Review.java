package com.example.myapplication.user_profile_page.user_profile_fragments;

public class Review {
    private String userId;
    private String username;
    private float rating;
    private String feedback;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String userId, String username, float rating, String feedback) {
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.feedback = feedback;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public float getRating() {
        return rating;
    }

    public String getFeedback() {
        return feedback;
    }
}
