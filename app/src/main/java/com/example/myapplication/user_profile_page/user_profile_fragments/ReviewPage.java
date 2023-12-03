package com.example.myapplication.user_profile_page.user_profile_fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewPage extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText reviewEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_review_page);

        setupToolbar();
        initializeUIElements();

        Button submitReviewButton = findViewById(R.id.submitReviewButton);
        submitReviewButton.setOnClickListener(view -> submitReview());
    }

    /**
     * Sets up the toolbar with the back button and its icon.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.review_page_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Tint the default back arrow icon to white
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);
        }
    }

    /**
     * Initializes the UI elements like rating bar and review edit text.
     */
    private void initializeUIElements() {
        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
    }

    /**
     * Handles the logic to submit a review to the Firebase database.
     */
    private void submitReview() {
        float rating = ratingBar.getRating();
        String reviewText = reviewEditText.getText().toString().trim();

        String reviewedUserId = getIntent().getStringExtra("reviewed_user_id");
        if (reviewedUserId == null) {
            Toast.makeText(ReviewPage.this, "Reviewed User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(ReviewPage.this, "Current User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch the current user's name from Firebase
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserId);
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String fullName = (firstName != null && lastName != null) ? firstName + " " + lastName : "Unknown User";

                // Now that we have the current user's name, we can proceed to find or create the review
                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("User").child(reviewedUserId).child("reviews");

                reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                        DataSnapshot existingReviewSnapshot = null;

                        // Check if the current user has already posted a review
                        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
                            String reviewerUserId = reviewSnapshot.child("userId").getValue(String.class);
                            if (currentUserId.equals(reviewerUserId)) {
                                existingReviewSnapshot = reviewSnapshot;
                                break;
                            }
                        }

                        DatabaseReference reviewToUpdateRef;
                        if (existingReviewSnapshot != null) {
                            // If the current user has already reviewed, update the existing review
                            reviewToUpdateRef = existingReviewSnapshot.getRef();
                        } else {
                            // If the current user has not reviewed yet, create a new review entry
                            reviewToUpdateRef = reviewsRef.push();
                        }

                        // Construct the review object with current user's info and the new review details
                        Review review = new Review(currentUserId, fullName, rating, reviewText);

                        // Set the value to either update existing or create a new review
                        reviewToUpdateRef.setValue(review)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ReviewPage.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(ReviewPage.this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ReviewPage.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReviewPage.this, "Failed to retrieve user details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Close the activity and return to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
