package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.myapplication.provider_fragments.ChatFragment;
import com.example.myapplication.provider_fragments.PostFragment;
import com.example.myapplication.provider_fragments.ListingsFragment;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageView;

import com.example.myapplication.notifications.NotificationWorker;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth firebaseAuth;


    public void scheduleWorker(String token) {

        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.HOURS);
        Data data = new Data.Builder()
                .putString("token", token)
                .build();

        PeriodicWorkRequest hourlyWork = workBuilder.setInputData(data).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("test", ExistingPeriodicWorkPolicy.KEEP, hourlyWork);

    }

    public void startWorkerProcess() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult();
                        scheduleWorker(token);

                    }
                });

    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // fragment_container is the ID of your FrameLayout where fragments will be displayed
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_provider);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView userProfileIcon = findViewById(R.id.icon_user_profile);
        userProfileIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        ImageView addressIcon = findViewById(R.id.icon_address);
        addressIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));

        userProfileIcon.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, UserProfile.class);
            startActivity(intent);
        });

        addressIcon.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, SavedAddresses.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set OnItemSelectedListener to handle selection events
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_chat) {
                replaceFragment(new ChatFragment());
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

        /*
        connectToFirebase();
        startWorkerProcess();


        Button loginPageButton = findViewById(R.id.loginPage);
        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // when clicked bring user to login page
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });


        // grabbing the login button
        Button registerPageButton = findViewById(R.id.registerPage);
        registerPageButton.setOnClickListener(new View.OnClickListener() {
            @Override

            // when clicked bring user to login page
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    protected void connectToFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("test");
    }
    }

}
*/
    }
}

