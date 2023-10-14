package com.example.myapplication.user_profile_page;

import android.os.Bundle;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.user_profile_fragments.Review;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    // Define UI components
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileViewPageAdapter ProfileViewPageAdapter;

    // Firebase database reference to "user_reviews"
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_reviews");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize RatingBar UI component
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        // Fetch user reviews and set the average rating on the RatingBar
        fetchReviewsAndSetRating(ratingBar);

        // Initialize TabLayout and ViewPager2 UI components
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        ProfileViewPageAdapter = new ProfileViewPageAdapter(this);
        viewPager2.setAdapter(ProfileViewPageAdapter);

        // Set listener to switch viewPager's page when a tab is selected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            // These methods can be overridden if you want to add behavior on unselect or reselect.
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });

        // Register callback to update tab's selection when a page is swiped in the viewPager
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }

    /**
     * Fetches reviews from the database and calculates the average rating to be set on the RatingBar.
     *
     * @param ratingBar The RatingBar where the average rating will be displayed.
     */
    private void fetchReviewsAndSetRating(RatingBar ratingBar) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Review> reviewList = new ArrayList<>();

                // Extract each review from the database snapshot and add it to the list
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Review review = extractReviewFromSnapshot(userSnapshot);
                    reviewList.add(review);
                }

                // Compute the average rating from the list and set it on the RatingBar
                float averageRating = computeAverageRating(reviewList);
                ratingBar.setRating(averageRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error, e.g. log it or show an error message to the user
            }
        });
    }

    /**
     * Extracts a review from a database snapshot.
     *
     * @param userSnapshot The DataSnapshot representing a single review.
     * @return A Review object populated with data from the snapshot.
     */
    private Review extractReviewFromSnapshot(DataSnapshot userSnapshot) {
        String userId = userSnapshot.getKey();
        String userName = userSnapshot.child("username").getValue(String.class);
        Float ratingValue = userSnapshot.child("rating").getValue(Float.class);
        float rating = (ratingValue != null) ? ratingValue : 0.0f;
        String feedback = userSnapshot.child("feedback").getValue(String.class);
        return new Review(userId, userName, rating, feedback);
    }

    /**
     * Computes the average rating from a list of reviews.
     *
     * @param reviewList The list of reviews to compute the average rating from.
     * @return The average rating.
     */
    private float computeAverageRating(List<Review> reviewList) {
        long totalRatings = 0;
        for (Review review : reviewList) {
            totalRatings += review.getRating();
        }
        float averageRating = totalRatings / (float) reviewList.size();

        // Return 0 if the result is NaN (e.g., if reviewList is empty), otherwise return the average.
        return Float.isNaN(averageRating) ? 0 : averageRating;
    }
}
