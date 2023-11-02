package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class UserListingActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    private ListView listingsListView;
    private ArrayAdapter<String> listingsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);
        listingsListView = findViewById(R.id.listingsListView);

        // Initialize Firebase components
        connectToFirebase();

        // Create an adapter for the ListView
        listingsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listingsListView.setAdapter(listingsAdapter);

        // Set a click listener for the ListView items
        listingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item's listing details
                String listingDetails = listingsAdapter.getItem(position);

                // Start the EditListingActivity with the listing details
                Intent intent = new Intent(UserListingActivity.this, EditDeleteListingActivity.class);
                intent.putExtra("listingDetails", listingDetails);
                startActivity(intent);
            }
        });

        // grabbing the return to success button to add an on-click listener
        Button returnSuccess = findViewById(R.id.returnSuccess);
        returnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when return is clicked
            public void onClick(View v) {
                // going back to the success page
                Intent loginIntent = new Intent(UserListingActivity.this, SuccessActivity.class);
                startActivity(loginIntent);
            }
        });
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
                ArrayList<String> listings = new ArrayList<>();

                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    if (isCurrentUserListing(listingSnapshot, currentUserId)) {
                        String listingDetails = getListingDetails(listingSnapshot);
                        listings.add(listingDetails);
                    }
                }

                displayListings(listings);
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

    private void displayListings(ArrayList<String> listings) {
        listingsAdapter.clear();
        listingsAdapter.addAll(listings);
    }

    private void handleDataRetrievalError() {
        String errorMessage = "An error occurred while retrieving data.";
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}

