package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.notifications.CheckListingsService;
import com.example.myapplication.notifications.NotificationsManager;
import com.example.myapplication.provider_fragments.ChatFragment;
import com.example.myapplication.provider_fragments.ListingsFragment;
import com.example.myapplication.provider_fragments.PostFragment;
import com.example.myapplication.receiver_fragments.ReceiverChatFragment;
import com.example.myapplication.receiver_fragments.ReceiverListingFragment;
import com.example.myapplication.receiver_fragments.SearchFragment;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SuccessActivity extends AppCompatActivity implements PostFragment.OnPostInteractionListener {
    private FirebaseAuth auth;
    private BottomNavigationView bottomNavigationView;
    private NotificationsManager notificationsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToFirebase();
       //Ask permission for notification
        askNotificationPermission();
        //Setup service to send notification
        setupNotificationService();
        // Check user role and setup UI accordingly
        checkUserRoleAndSetupUI(savedInstanceState);
    }

    private void askNotificationPermission() {
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {


                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            }else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    private void setupNotificationService(){
        notificationsManager = new NotificationsManager();
        notificationsManager.createToken();

        Intent serviceIntent = new Intent(this, CheckListingsService.class);
        startService(serviceIntent);
    }

    // Connect to Firebase
    private void connectToFirebase() {
        auth = FirebaseAuth.getInstance();
    }

    // Check user role and setup UI
    private void checkUserRoleAndSetupUI(Bundle savedInstanceState) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("User/" + uid + "/role")
                .addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            setupUIBasedOnRole(dataSnapshot.getValue(String.class), savedInstanceState);
                        }
                    }
                });
    }

    // Setup UI based on user role
    private void setupUIBasedOnRole(String userRole, Bundle savedInstanceState) {
        if ("Provider".equals(userRole)) {
            setContentView(R.layout.activity_success_provider);
        } else {
            setContentView(R.layout.activity_success_receiver);
        }

        setupToolbar();
        setupIconColors();
        setupIconActions();
        setupBottomNavigation(userRole, savedInstanceState);
    }

    // Setup toolbar
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // Setup icon colors
    private void setupIconColors() {
        int whiteColor = ContextCompat.getColor(SuccessActivity.this, android.R.color.white);
        ImageView userProfileIcon = findViewById(R.id.icon_user_profile);
        userProfileIcon.setColorFilter(whiteColor);
        ImageView addressIcon = findViewById(R.id.icon_address);
        addressIcon.setColorFilter(whiteColor);
        ImageView logoutIcon = findViewById(R.id.icon_log_out);
        logoutIcon.setColorFilter(whiteColor);
    }

    // Setup bottom navigation and its listener
    private void setupBottomNavigation(String userRole, Bundle savedInstanceState) {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if ("Provider".equals(userRole)) {
            bottomNavigationView.setOnItemSelectedListener(new ProviderNavigationListener());
        } else {
            bottomNavigationView.setOnItemSelectedListener(new ReceiverNavigationListener());
        }

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_listings); // Default tab
        }
    }

    // Setup icon actions
    private void setupIconActions() {
        setupIconClickAction(R.id.icon_user_profile, UserProfile.class);
        setupIconClickAction(R.id.icon_address, SavedAddresses.class);
        setupLogoutIconClickAction(R.id.icon_log_out);
    }

    // General method for setting up icon click actions
    private void setupIconClickAction(int iconId, Class<?> destination) {
        findViewById(iconId).setOnClickListener(v -> {
            if (destination == null) {
                new AlertDialog.Builder(this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("No", null)
                        .setNegativeButton("Yes", (dialog, which) -> logout())
                        .show();
            } else {
                startActivity(new Intent(this, destination));
            }
        });
    }

    // Special handling for logout icon click action
    private void setupLogoutIconClickAction(int iconId) {
        findViewById(iconId).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes", (dialog, which) -> {
                    logout();
                    Toast.makeText(SuccessActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .show());
    }

    // Logout method
    private void logout() {
        auth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Replace fragment in container
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Listener for provider navigation
    private class ProviderNavigationListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        }
    }

    // Listener for receiver navigation
    private class ReceiverNavigationListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        }
    }


    // Post interaction callback
    @Override
    public void onPostCompleted() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_listings); // Switch to Listings tab
    }

    // Simplified ValueEventListener for cleaner code
    private abstract class ValueEventListenerAdapter implements ValueEventListener {
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle cancellation
            setContentView(R.layout.activity_success);
        }
    }
}
