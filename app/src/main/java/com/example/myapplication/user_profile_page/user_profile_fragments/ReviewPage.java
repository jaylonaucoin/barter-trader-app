package com.example.myapplication.user_profile_page.user_profile_fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class ReviewPage extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText reviewEditText;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Close the activity and go back to UserProfile
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_review_page);

        Toolbar toolbar = findViewById(R.id.review_page_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setTitle("Review");

        // Tint the default back arrow icon to white
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);
        }

        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        Button submitReviewButton = findViewById(R.id.submitReviewButton);

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the user's rating and review text
                float rating = ratingBar.getRating();
                String reviewText = reviewEditText.getText().toString().trim();
                // add user123
                String userId = "user123";

                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("user_reviews").child(userId);

                // Update the appropriate rating category based on the rating value
                userReference.child("username").setValue("Roshan");
                userReference.child("rating").setValue(rating);
                userReference.child("feedback").setValue(reviewText);


                // Check if the rating bar is not empty
                if (rating > 0) {
                    Toast.makeText(ReviewPage.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ReviewPage.this, "Please set a rating!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}