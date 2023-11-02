package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserListingActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    private TextView listingsTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);
        listingsTextView = findViewById(R.id.listingsTextView);

        // Initialize Firebase components
        connectToFirebase();
    }

    private void connectToFirebase() {
        // Initialize Firebase instances
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
        auth = FirebaseAuth.getInstance();
        fetchAndDisplayListings();
    }

    private void fetchAndDisplayListings() {
        // Get the currently logged-in user's ID
        String currentUserId = auth.getCurrentUser().getUid();

        // Listen for changes in the database
        firebaseDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder listings = new StringBuilder();
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    // Get the unique listing ID for each listing
                    String listingId = listingSnapshot.getKey();

                    // Parse listing data
                    String userId = listingSnapshot.child("User ID").getValue(String.class);

                    // Check if the listing belongs to the currently logged-in user
                    if (userId != null && userId.equals(currentUserId)) {
                        String address = listingSnapshot.child("Address").getValue(String.class);
                        String condition = listingSnapshot.child("Condition").getValue(String.class);
                        String description = listingSnapshot.child("Description").getValue(String.class);
                        String exchangePreference = listingSnapshot.child("Exchange Preference").getValue(String.class);
                        String productName = listingSnapshot.child("Product Name").getValue(String.class);

                        // Use the listing ID in your display or any other logic
                        listings.append("Product Name: ").append(productName).append("\n");
                        listings.append("Description: ").append(description).append("\n");
                        listings.append("Condition: ").append(condition).append("\n");
                        listings.append("Exchange Preference: ").append(exchangePreference).append("\n");
                        listings.append("Address: ").append(address).append("\n");
                    }
                }
                listingsTextView.setText(listings.toString()); // Display listings in the TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String errorMessage = "An error occurred while retrieving data.";
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
