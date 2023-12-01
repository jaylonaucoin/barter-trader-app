package com.example.myapplication.user_profile_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.messaging.MessagingActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    // Define UI components
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileViewPageAdapter profileViewPageAdapter;
    RatingBar ratingBar;
    ImageView messageButton;
    String userId;

    // Firebase database reference to User node
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("User");

    public void onCloseClick(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize RatingBar UI component
        ratingBar = findViewById(R.id.ratingBar);

        messageButton = findViewById(R.id.messageButton);

        // Fetch and set user ID and reviews
        userId = fetchUserUID();

        setupMessageButton(userId);

        // Fetch the TextView for the username and set it
        TextView usernameTextView = findViewById(R.id.username);
        fetchUsername(usernameTextView);

        // Initialize TabLayout and ViewPager2 UI components
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        profileViewPageAdapter = new ProfileViewPageAdapter(this, userId);
        viewPager2.setAdapter(profileViewPageAdapter);

        // Set listener to switch viewPager's page when a tab is selected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

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

    private void setupMessageButton(String profileUserId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !currentUser.getUid().equals(profileUserId)) {
            // The profile being viewed is not the current user's, show the message button
            messageButton.setVisibility(View.VISIBLE);
            messageButton.setOnClickListener(view -> {
                // Start the MessagingActivity with the UID of the user's profile being viewed
                Intent intent = new Intent(UserProfile.this, MessagingActivity.class);
                intent.putExtra("RECEIVER_ID", profileUserId); // Pass the profileUserId as RECEIVER_ID
                startActivity(intent);
            });
        }
    }


    private void fetchUsername(TextView usernameTextView) {
        // Get UID from Intent or default to current user's UID
        String uid = getIntent().getStringExtra("uid");
        if (uid == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                uid = user.getUid();
            }
        }

        if (uid != null) {
            DatabaseReference userReference = dbRef.child(uid);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    if (firstName != null && lastName != null) {
                        String fullName = firstName + " " + lastName;
                        usernameTextView.setText(fullName);
                    } else {
                        usernameTextView.setText("N/A");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private String fetchUserUID() {
        // Check for UID passed via Intent
        String uid = getIntent().getStringExtra("uid");
        if (uid != null) {
            // Use the UID from the Intent
            fetchReviewsAndSetRating(uid);
            return uid;
        } else {
            // Default to the current user's UID
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                fetchReviewsAndSetRating(user.getUid());
            }
        }

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    private void fetchReviewsAndSetRating(String profileUserId) {
        DatabaseReference reviewsRef = dbRef.child(profileUserId).child("reviews");

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Float rating = reviewSnapshot.child("rating").getValue(Float.class);
                    if (rating != null) {
                        totalRating += rating;
                        count++;
                    }
                }

                float averageRating = count > 0 ? totalRating / count : 0;
                ratingBar.setRating(averageRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
}
