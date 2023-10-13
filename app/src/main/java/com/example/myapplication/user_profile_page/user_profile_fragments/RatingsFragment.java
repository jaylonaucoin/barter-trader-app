package com.example.myapplication.user_profile_page.user_profile_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingsFragment extends Fragment {

    private RatingBar ratingBar;
    private ProgressBar fiveStarProgressBar, fourStarProgressBar, threeStarProgressBar, twoStarProgressBar, oneStarProgressBar;
    private TextView fiveStarCount, fourStarCount, threeStarCount, twoStarCount, oneStarCount, decimalRatingText;
    private List<Review> reviewList = new ArrayList<>();
    private ReviewsAdapter adapter;

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_reviews");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_ratings, container, false);
        initUI(view);
        fetchReviewsFromFirebase();
        return view;
    }

    private void initUI(View view) {
        // Initialize RecyclerView and adapter
        RecyclerView recyclerView = view.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReviewsAdapter(reviewList);
        recyclerView.setAdapter(adapter);

        // Initialize other UI elements
        ratingBar = view.findViewById(R.id.ratingBar);
        initializeProgressBarAndTextView(view);

        ImageButton reviewButton = view.findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(v -> startActivity(new Intent(requireContext(), ReviewPage.class)));

    }

    private void initializeProgressBarAndTextView(View view) {
        fiveStarProgressBar = view.findViewById(R.id.fiveStarProgressBar);
        fourStarProgressBar = view.findViewById(R.id.fourStarProgressBar);
        threeStarProgressBar = view.findViewById(R.id.threeStarProgressBar);
        twoStarProgressBar = view.findViewById(R.id.twoStarProgressBar);
        oneStarProgressBar = view.findViewById(R.id.oneStarProgressBar);

        fiveStarCount = view.findViewById(R.id.fiveStarCount);
        fourStarCount = view.findViewById(R.id.fourStarCount);
        threeStarCount = view.findViewById(R.id.threeStarCount);
        twoStarCount = view.findViewById(R.id.twoStarCount);
        oneStarCount = view.findViewById(R.id.oneStarCount);
        decimalRatingText = view.findViewById(R.id.decimalRatingText);
    }

    private void fetchReviewsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Review review = extractReviewFromSnapshot(userSnapshot);
                    reviewList.add(review);
                }
                updateUIElements();
                adapter.notifyDataSetChanged();
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
        float rating = (ratingValue != null) ? ratingValue : 0.0f;
        String feedback = userSnapshot.child("feedback").getValue(String.class);
        return new Review(userId, userName, rating, feedback);
    }


    private void updateUIElements() {
        long totalRatings = 0;
        long fiveStar = 0, fourStar = 0, threeStar = 0, twoStar = 0, oneStar = 0;

        for (Review review : reviewList) {
            // Count each star rating
            float rating = review.getRating();
            if (rating == 5) {
                fiveStar++;
            } else if (rating == 4) {
                fourStar++;
            } else if (rating == 3) {
                threeStar++;
            } else if (rating == 2) {
                twoStar++;
            } else if (rating == 1) {
                oneStar++;
            }
            // Calculate total ratings
            totalRatings += rating;
        }

        // Calculate average rating
        float averageRating = totalRatings / (float) reviewList.size();

        if (Float.isNaN(averageRating)) {
            averageRating = 0;
        }


        int totalStarRatings = (int) (fiveStar + fourStar + threeStar + twoStar + oneStar);
        int fiveStarPercentage = totalStarRatings > 0 ? (int) ((fiveStar / (float) totalStarRatings) * 100) : 0;
        int fourStarPercentage = totalStarRatings > 0 ? (int) ((fourStar / (float) totalStarRatings) * 100) : 0;
        int threeStarPercentage = totalStarRatings > 0 ? (int) ((threeStar / (float) totalStarRatings) * 100) : 0;
        int twoStarPercentage = totalStarRatings > 0 ? (int) ((twoStar / (float) totalStarRatings) * 100) : 0;
        int oneStarPercentage = totalStarRatings > 0 ? (int) ((oneStar / (float) totalStarRatings) * 100) : 0;

        // Update progress bars
        fiveStarProgressBar.setProgress(fiveStarPercentage);
        fourStarProgressBar.setProgress(fourStarPercentage);
        threeStarProgressBar.setProgress(threeStarPercentage);
        twoStarProgressBar.setProgress(twoStarPercentage);
        oneStarProgressBar.setProgress(oneStarPercentage);

        fiveStarCount.setText(String.valueOf(fiveStar));
        fourStarCount.setText(String.valueOf(fourStar));
        threeStarCount.setText(String.valueOf(threeStar));
        twoStarCount.setText(String.valueOf(twoStar));
        oneStarCount.setText(String.valueOf(oneStar));

        ratingBar.setRating(averageRating);
        decimalRatingText.setText(String.format("%.2f out of 5", averageRating));
    }

}
