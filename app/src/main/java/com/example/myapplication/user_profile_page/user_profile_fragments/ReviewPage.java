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
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String fullName = (firstName != null && lastName != null) ? firstName + " " + lastName : "Unknown User";

                    DatabaseReference reviewRef = userReference.child("reviews").push();
                    Review review = new Review(userId, fullName, rating, reviewText);
                    reviewRef.setValue(review);

                    if (rating > 0) {
                        Toast.makeText(ReviewPage.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ReviewPage.this, "Please set a rating!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        } else {
            Toast.makeText(ReviewPage.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
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
