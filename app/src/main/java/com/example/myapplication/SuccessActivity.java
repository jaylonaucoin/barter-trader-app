package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.messaging.MessagingActivity;
import com.example.myapplication.provider_fragments.ChatFragment;
import com.example.myapplication.provider_fragments.ListingsFragment;
import com.example.myapplication.provider_fragments.PostFragment;
import com.example.myapplication.reciever_fragments.ReceiverChatFragment;
import com.example.myapplication.reciever_fragments.ReceiverListingFragment;
import com.example.myapplication.reciever_fragments.SearchFragment;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SuccessActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    String name;


    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // fragment_container is the ID of your FrameLayout where fragments will be displayed
                .commit();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        connectToFirebase();

        // Get the user's name from auth and DB
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        firebaseDBRef = firebaseDB.getReference("User/" + uid); // Reference to the user's data in the database

        // Check the user's role in the database
        firebaseDBRef.child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userRole = dataSnapshot.getValue(String.class);

                    if ("Provider".equals(userRole)) {
                        // User is a Provider, set the content view to activity_main_provider
                        setContentView(R.layout.activity_success_provider);

                        Toolbar toolbar = findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);

                        ImageView userProfileIcon = findViewById(R.id.icon_user_profile);
                        userProfileIcon.setColorFilter(ContextCompat.getColor(SuccessActivity.this, android.R.color.white));
                        ImageView addressIcon = findViewById(R.id.icon_address);
                        addressIcon.setColorFilter(ContextCompat.getColor(SuccessActivity.this, android.R.color.white));
                        ImageView logoutIcon = findViewById(R.id.icon_log_out);
                        logoutIcon.setColorFilter(ContextCompat.getColor(SuccessActivity.this, android.R.color.white));

                        logoutIcon.setOnClickListener(v -> new AlertDialog.Builder(SuccessActivity.this)
                                .setTitle("Logout Confirmation")
                                .setMessage("Are you sure you want to log out?")
                                .setPositiveButton("No", null) // "No" button first
                                .setNegativeButton("Yes", (dialog, which) -> {
                                    auth.signOut();
                                    Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }) // "Yes" button second
                                .show());


                        userProfileIcon.setOnClickListener(view -> {

                            Intent intent = new Intent(SuccessActivity.this, UserProfile.class);
                            startActivity(intent);
                        });

                        addressIcon.setOnClickListener(view -> {

                            Intent intent = new Intent(SuccessActivity.this, SavedAddresses.class);
                            startActivity(intent);
                        });

                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

                        // Set OnItemSelectedListener to handle selection events
                        bottomNavigationView.setOnItemSelectedListener(item -> {
                            int itemId = item.getItemId();

                            if (itemId == R.id.navigation_chat) {
                                replaceFragment(new ChatFragment());
                                return true;
                            } else if (itemId == R.id.navigation_post) {
                                replaceFragment(new PostFragment());
                                return true;
                            } else if (itemId == R.id.navigation_listings) {
                                replaceFragment(new ListingsFragment());
                                return true;
                            }

                            return false;
                        });

                        // Default tab
                        if (savedInstanceState == null) {
                            bottomNavigationView.setSelectedItemId(R.id.navigation_listings);
                        }

                    } else {

                        setContentView(R.layout.activity_success_receiver);

                        Toolbar toolbar = findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);

                        ImageView userProfileIcon = findViewById(R.id.icon_user_profile);
                        userProfileIcon.setColorFilter(ContextCompat.getColor(SuccessActivity.this, android.R.color.white));
                        ImageView addressIcon = findViewById(R.id.icon_address);
                        addressIcon.setColorFilter(ContextCompat.getColor(SuccessActivity.this, android.R.color.white));
                        ImageView logoutIcon = findViewById(R.id.icon_log_out);
                        logoutIcon.setColorFilter(ContextCompat.getColor(SuccessActivity.this, android.R.color.white));

                        logoutIcon.setOnClickListener(v -> new AlertDialog.Builder(SuccessActivity.this)
                                .setTitle("Logout Confirmation")
                                .setMessage("Are you sure you want to log out?")
                                .setPositiveButton("No", null) // "No" button first
                                .setNegativeButton("Yes", (dialog, which) -> {
                                    auth.signOut();
                                    Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }) // "Yes" button second
                                .show());

                        userProfileIcon.setOnClickListener(view -> {

                            Intent intent = new Intent(SuccessActivity.this, UserProfile.class);
                            startActivity(intent);
                        });

                        addressIcon.setOnClickListener(view -> {

                            Intent intent = new Intent(SuccessActivity.this, SavedAddresses.class);
                            startActivity(intent);
                        });

                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

                        // Set OnItemSelectedListener to handle selection events
                        bottomNavigationView.setOnItemSelectedListener(item -> {
                            int itemId = item.getItemId();

                            if (itemId == R.id.navigation_chat) {
                                replaceFragment(new ReceiverChatFragment());
                                return true;
                            } else if (itemId == R.id.navigation_search) {
                                replaceFragment(new SearchFragment());
                                return true;
                            } else if (itemId == R.id.navigation_listings) {
                                replaceFragment(new ReceiverListingFragment());
                                return true;
                            }

                            return false;
                        });

                        // Default tab
                        if (savedInstanceState == null) {
                            bottomNavigationView.setSelectedItemId(R.id.navigation_listings);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // For example, you can set the content view to activity_success as a default
                setContentView(R.layout.activity_success);
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
