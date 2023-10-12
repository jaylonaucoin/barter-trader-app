package com.example.myapplication.user_profile_page.user_profile_fragments;

public class Review {
    private float rating;
    private String reviewText;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(float rating, String reviewText) {
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}
