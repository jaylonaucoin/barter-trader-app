package com.example.myapplication.user_profile_page.user_profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private final List<Review> reviewList = new ArrayList<>();
    private ReviewsAdapter adapter;
    private DatabaseReference userReviewsRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_ratings, container, false);
        initUI(view);
        setupFirebaseReference();
        fetchCurrentUserReviews();
        return view;
    }

    private void initUI(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReviewsAdapter(reviewList);
        recyclerView.setAdapter(adapter);

        ratingBar = view.findViewById(R.id.ratingBar);
        decimalRatingText = view.findViewById(R.id.decimalRatingText);
    }

    private void setupFirebaseReference() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId != null) {
            userReviewsRef = FirebaseDatabase.getInstance().getReference()
                    .child("User")
                    .child(currentUserId)
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
                    int count = 0;
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);
                        if (review != null) {
                            reviewList.add(review);
                            totalRating += review.getRating();
                            count++;
                        }
                    }
                    float averageRating = count > 0 ? totalRating / count : 0;
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
}
