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
import java.util.Optional;

public class UserProfile extends AppCompatActivity {

    // UI Components
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private RatingBar ratingBar;
    private ImageView messageButton;
    private String userId;

    // Firebase reference
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("User");

    // Closes the activity
    public void onCloseClick(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI components
        initializeUI();

        // Fetch user ID from intent or Firebase
        userId = fetchUserUID();

        // Setup the message button functionality
        setupMessageButton(userId);

        // Fetch and set the username
        TextView usernameTextView = findViewById(R.id.username);
        fetchUsername(usernameTextView);

        // Setup tab layout and view pager
        setupTabs();
    }

    // Initialize UI components
    private void initializeUI() {
        ratingBar = findViewById(R.id.ratingBar);
        messageButton = findViewById(R.id.messageButton);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        ProfileViewPageAdapter profileViewPageAdapter = new ProfileViewPageAdapter(this, userId);
        viewPager2.setAdapter(profileViewPageAdapter);
    }

    // Setup message button functionality
    private void setupMessageButton(String profileUserId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !currentUser.getUid().equals(profileUserId)) {
            messageButton.setVisibility(View.VISIBLE);
            messageButton.setOnClickListener(view -> startMessagingActivity(profileUserId));
        }
    }

    // Start MessagingActivity
    private void startMessagingActivity(String receiverId) {
        Intent intent = new Intent(UserProfile.this, MessagingActivity.class);
        intent.putExtra("RECEIVER_ID", receiverId);
        startActivity(intent);
    }

    // Fetches and sets the username in the TextView
    private void fetchUsername(TextView usernameTextView) {
        String uid = Optional.ofNullable(getIntent().getStringExtra("uid"))
                .orElseGet(() -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    return user != null ? user.getUid() : null;
                });

        if (uid != null) {
            DatabaseReference userReference = dbRef.child(uid);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    setUsernameTextView(dataSnapshot, usernameTextView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    // Set the username in the TextView
    private void setUsernameTextView(DataSnapshot dataSnapshot, TextView usernameTextView) {
        String firstName = dataSnapshot.child("firstName").getValue(String.class);
        String lastName = dataSnapshot.child("lastName").getValue(String.class);

        if (firstName != null && lastName != null) {
            String fullName = firstName + " " + lastName;
            usernameTextView.setText(fullName);
        } else {
            usernameTextView.setText("N/A");
        }
    }

    // Fetches user UID
    private String fetchUserUID() {
        String uid = getIntent().getStringExtra("uid");
        if (uid == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user != null ? user.getUid() : null;
        }

        if (uid != null) {
            fetchReviewsAndSetRating(uid);
        }
        return uid;
    }

    // Fetch user reviews and set rating
    private void fetchReviewsAndSetRating(String profileUserId) {
        DatabaseReference reviewsRef = dbRef.child(profileUserId).child("reviews");
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateRatingBar(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    // Update the RatingBar based on user reviews
    private void updateRatingBar(DataSnapshot dataSnapshot) {
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

    // Setup the tab layout and view pager for the profile
    private void setupTabs() {
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

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }
}
