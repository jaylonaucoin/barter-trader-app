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
        String currentUserId = auth.getCurrentUser().getUid();

        firebaseDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder listings = new StringBuilder();

                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    if (isCurrentUserListing(listingSnapshot, currentUserId)) {
                        String listingDetails = getListingDetails(listingSnapshot);
                        listings.append(listingDetails);
                    }
                }

                displayListings(listings.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDataRetrievalError();
            }
        });
    }

    private boolean isCurrentUserListing(DataSnapshot listingSnapshot, String currentUserId) {
        String userId = listingSnapshot.child("User ID").getValue(String.class);
        return userId != null && userId.equals(currentUserId);
    }

    private String getListingDetails(DataSnapshot listingSnapshot) {
        String productName = listingSnapshot.child("Product Name").getValue(String.class);
        String description = listingSnapshot.child("Description").getValue(String.class);
        String condition = listingSnapshot.child("Condition").getValue(String.class);
        String exchangePreference = listingSnapshot.child("Exchange Preference").getValue(String.class);
        String address = listingSnapshot.child("Address").getValue(String.class);

        StringBuilder listingDetails = new StringBuilder();
        listingDetails.append("Product Name: ").append(productName).append("\n");
        listingDetails.append("Description: ").append(description).append("\n");
        listingDetails.append("Condition: ").append(condition).append("\n");
        listingDetails.append("Exchange Preference: ").append(exchangePreference).append("\n");
        listingDetails.append("Address: ").append(address).append("\n");

        return listingDetails.toString();
    }

    private void displayListings(String listings) {
        listingsTextView.setText(listings);
    }

    private void handleDataRetrievalError() {
        String errorMessage = "An error occurred while retrieving data.";
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

}
