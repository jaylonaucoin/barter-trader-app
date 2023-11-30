package com.example.myapplication.user_profile_page;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    // Define UI components
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileViewPageAdapter profileViewPageAdapter;
    RatingBar ratingBar;

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

        // Fetch and set user ID and reviews
        fetchUserUID();

        // Fetch the TextView for the username and set it
        TextView usernameTextView = findViewById(R.id.username);
        fetchUsername(usernameTextView);

        // Initialize TabLayout and ViewPager2 UI components
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        profileViewPageAdapter = new ProfileViewPageAdapter(this);
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
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void fetchUsername(TextView usernameTextView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userReference = dbRef.child(user.getUid());

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

    private void fetchUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String profileUserId = user.getUid();
            fetchReviewsAndSetRating(profileUserId);
        }
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
