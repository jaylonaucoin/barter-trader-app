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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RatingsFragment extends Fragment {

    private RatingBar ratingBar;
    private TextView decimalRatingText;
    private ProgressBar fiveStarProgressBar, fourStarProgressBar, threeStarProgressBar, twoStarProgressBar, oneStarProgressBar;
    private TextView fiveStarCount, fourStarCount, threeStarCount, twoStarCount, oneStarCount;
    private final List<Review> reviewList = new ArrayList<>();
    private ReviewsAdapter adapter;
    private DatabaseReference userReviewsRef;
    private ImageButton reviewButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_ratings, container, false);
        initUI(view);
        setupFirebaseReference();
        fetchCurrentUserReviews();
        checkIfCurrentUser(view); // New method to check if the current user is viewing their own profile
        return view;
    }


    private void initUI(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReviewsAdapter(reviewList);
        recyclerView.setAdapter(adapter);

        ratingBar = view.findViewById(R.id.ratingBar);
        decimalRatingText = view.findViewById(R.id.decimalRatingText);

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

        // Set up the review button
        // Review button to navigate to ReviewPage

        reviewButton = view.findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReviewPage.class);

            // Get the user ID from arguments, or use the current user's ID if null
            String uid = getArguments() != null ? getArguments().getString("uid") : null;
            if (uid == null) {
                uid = FirebaseAuth.getInstance().getUid();
            }

            // Pass the user ID to ReviewPage
            intent.putExtra("reviewed_user_id", uid);
            startActivity(intent);
        });
    }

    private void checkIfCurrentUser(View view) {
        assert getArguments() != null;
        String profileUserId = getArguments().getString("uid"); // Get the user ID passed to the fragment
        String currentUserId = FirebaseAuth.getInstance().getUid();

        if (currentUserId != null && currentUserId.equals(profileUserId)) {
            // Current user is viewing their own profile, hide the review button
            reviewButton.setVisibility(View.GONE);
        }
    }



    private void setupFirebaseReference() {
        // Retrieve user ID from arguments if it exists
        Bundle args = getArguments();
        String userId = args != null ? args.getString("uid") : null;

        // If no user ID in arguments, use the current user's ID
        if (userId == null) {
            userId = FirebaseAuth.getInstance().getUid();
        }

        // Set up Firebase reference with the determined user ID
        if (userId != null) {
            userReviewsRef = FirebaseDatabase.getInstance().getReference()
                    .child("User")
                    .child(userId)
                    .child("reviews");
        }
    }


    private void fetchCurrentUserReviews() {
        if (userReviewsRef != null) {
            userReviewsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reviewList.clear();
                    float totalRating = 0;
                    int[] starCounts = new int[5]; // From 1 to 5 stars
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);
                        if (review != null) {
                            reviewList.add(review);
                            totalRating += review.getRating();
                            if (review.getRating() >= 1 && review.getRating() <= 5) {
                                starCounts[(int) review.getRating() - 1]++;
                            }
                        }
                    }
                    updateStarCounts(starCounts);
                    float averageRating = reviewList.size() > 0 ? totalRating / reviewList.size() : 0;
                    ratingBar.setRating(averageRating);
                    decimalRatingText.setText(String.format(Locale.US, "%.2f out of 5", averageRating));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void updateStarCounts(int[] starCounts) {
        int totalReviews = reviewList.size();
        fiveStarCount.setText(String.format(Locale.US, "%d", starCounts[4]));
        fourStarCount.setText(String.format(Locale.US, "%d", starCounts[3]));
        threeStarCount.setText(String.format(Locale.US, "%d", starCounts[2]));
        twoStarCount.setText(String.format(Locale.US, "%d", starCounts[1]));
        oneStarCount.setText(String.format(Locale.US, "%d", starCounts[0]));

        // Update progress bars
        if (totalReviews > 0) {
            fiveStarProgressBar.setProgress((starCounts[4] * 100) / totalReviews);
            fourStarProgressBar.setProgress((starCounts[3] * 100) / totalReviews);
            threeStarProgressBar.setProgress((starCounts[2] * 100) / totalReviews);
            twoStarProgressBar.setProgress((starCounts[1] * 100) / totalReviews);
            oneStarProgressBar.setProgress((starCounts[0] * 100) / totalReviews);
        } else {
            fiveStarProgressBar.setProgress(0);
            fourStarProgressBar.setProgress(0);
            threeStarProgressBar.setProgress(0);
            twoStarProgressBar.setProgress(0);
            oneStarProgressBar.setProgress(0);
        }
    }
}
