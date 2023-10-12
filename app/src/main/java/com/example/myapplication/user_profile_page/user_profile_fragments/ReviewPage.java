package com.example.myapplication.user_profile_page.user_profile_fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
public class ReviewPage extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText reviewEditText;

    //private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_review_page);

        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        Button submitReviewButton = findViewById(R.id.submitReviewButton);

        // Initialize Firebase Database
        //databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the user's rating and review text
                float rating = ratingBar.getRating();
                String reviewText = reviewEditText.getText().toString().trim();

                // Check if the rating bar is not empty
                if (rating > 0) {
                    // Create a Review object
                    Review review = new Review(rating, reviewText);

                    // Generate a unique key for the review in Firebase
                    //String reviewId = databaseReference.push().getKey();

                    // Save the review to Firebase Database
                    //databaseReference.child(reviewId).setValue(review);

                    Toast.makeText(ReviewPage.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(ReviewPage.this, "Please set a rating!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}