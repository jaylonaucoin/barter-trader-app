package com.example.myapplication.user_profile_page;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.user_profile_fragments.RatingsFragment;
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


    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileViewPageAdapter ProfileViewPageAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_reviews");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RatingBar ratingBar = findViewById(R.id.ratingBar);

        fetchReviewsAndSetRating(ratingBar);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        ProfileViewPageAdapter = new ProfileViewPageAdapter(this);
        viewPager2.setAdapter(ProfileViewPageAdapter);

        // sets viewpage to their respective fragment using tab layout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // registers tab layout changes when swiping
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }

    private void fetchReviewsAndSetRating(RatingBar ratingBar) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Review> reviewList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Review review = extractReviewFromSnapshot(userSnapshot);
                    reviewList.add(review);
                }
                float averageRating = computeAverageRating(reviewList);
                ratingBar.setRating(averageRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private Review extractReviewFromSnapshot(DataSnapshot userSnapshot) {
        String userId = userSnapshot.getKey();
        String userName = userSnapshot.child("username").getValue(String.class);
        Float ratingValue = userSnapshot.child("rating").getValue(Float.class);
        float rating = (ratingValue != null) ? ratingValue.floatValue() : 0.0f;
        String feedback = userSnapshot.child("feedback").getValue(String.class);
        return new Review(userId, userName, rating, feedback);
    }

    private float computeAverageRating(List<Review> reviewList) {
        long totalRatings = 0;
        for (Review review : reviewList) {
            totalRatings += review.getRating();
        }
        float averageRating = totalRatings / (float) reviewList.size();
        if (Float.isNaN(averageRating)) {
            return 0;
        }
        return averageRating;
    }
}
