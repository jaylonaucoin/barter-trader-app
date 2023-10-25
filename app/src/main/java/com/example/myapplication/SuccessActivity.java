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

public class SuccessActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    String name;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // Initialize Firebase components
        connectToFirebase();

        // Get the user's name from auth and DB
        String uid = auth.getCurrentUser().getUid();
        firebaseDBRef = firebaseDB.getReference("User/" + uid); // Reference to the user's data in the database

        firebaseDBRef.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);

                // Find the welcome message TextView and set it with the user's name
                TextView welcomeMessage = findViewById(R.id.welcomeMessage);
                welcomeMessage.setText("Welcome " + name + "!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors or provide a default name
                name = "Unknown User";

                // Find the welcome message TextView and set it with the default name
                TextView welcomeMessage = findViewById(R.id.welcomeMessage);
                welcomeMessage.setText("Welcome " + name + "!");
            }
        });

        // grabbing the logout button to add an on-click listener
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when logout is clicked
            public void onClick(View v) {
                // signing out the user
                auth.signOut();
                Toast.makeText(SuccessActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // going back to the login page
                Intent loginIntent = new Intent(SuccessActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        // grabbing the listing button to add an on-click listener
        Button listingButton = findViewById(R.id.userListing);
        listingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when user listings is clicked
            public void onClick(View v) {
                Toast.makeText(SuccessActivity.this, "User Listings Page", Toast.LENGTH_SHORT).show();

                // going back to the listing page
                Intent listingIntent = new Intent(SuccessActivity.this, UserListingActivity.class);
                startActivity(listingIntent);
            }
        });
    }

    private void connectToFirebase(){
        // Initialize Firebase instances
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("User");
        auth = FirebaseAuth.getInstance();
    }
}
