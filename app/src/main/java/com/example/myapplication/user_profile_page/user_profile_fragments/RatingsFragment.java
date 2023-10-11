package com.example.myapplication.user_profile_page.user_profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

//import com.google.firebase.database.DatabaseReference;

import com.example.myapplication.R;
/*
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
*/

public class RatingsFragment extends Fragment {

    private RatingBar ratingBar;
    private ProgressBar fiveStarProgressBar, fourStarProgressBar, threeStarProgressBar, twoStarProgressBar, oneStarProgressBar;
    private TextView fiveStarCount, fourStarCount, threeStarCount, twoStarCount, oneStarCount;

    // private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        ratingBar = view.findViewById(R.id.ratingBar);
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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user_reviews");

        // Assuming userId is the unique identifier for the user whose reviews you want to retrieve
        String userId = "user123"; // Replace this with the actual user ID

        // Retrieve reviews data from Firebase
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Assuming the data structure in Firebase is like this:
                // "user_reviews" -> "userId" -> "fiveStar", "fourStar", "threeStar", "twoStar", "oneStar"
                long fiveStar = dataSnapshot.child("fiveStar").getValue(Long.class);
                long fourStar = dataSnapshot.child("fourStar").getValue(Long.class);
                long threeStar = dataSnapshot.child("threeStar").getValue(Long.class);
                long twoStar = dataSnapshot.child("twoStar").getValue(Long.class);
                long oneStar = dataSnapshot.child("oneStar").getValue(Long.class);

                // Update RatingBar and ProgressBar values
                long totalRatings = fiveStar + fourStar + threeStar + twoStar + oneStar;
                float averageRating = (5 * fiveStar + 4 * fourStar + 3 * threeStar + 2 * twoStar + oneStar) / (float) totalRatings;

                ratingBar.setRating(averageRating);
                fiveStarProgressBar.setProgress((int) (fiveStar * 100 / totalRatings));
                fourStarProgressBar.setProgress((int) (fourStar * 100 / totalRatings));
                threeStarProgressBar.setProgress((int) (threeStar * 100 / totalRatings));
                twoStarProgressBar.setProgress((int) (twoStar * 100 / totalRatings));
                oneStarProgressBar.setProgress((int) (oneStar * 100 / totalRatings));

                // Update TextViews
                fiveStarCount.setText(String.valueOf(fiveStar));
                fourStarCount.setText(String.valueOf(fourStar));
                threeStarCount.setText(String.valueOf(threeStar));
                twoStarCount.setText(String.valueOf(twoStar));
                oneStarCount.setText(String.valueOf(oneStar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    */
        return inflater.inflate(R.layout.fragment_user_profile_ratings, container, false);
    }
}