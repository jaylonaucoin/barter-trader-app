package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.notifications.NotificationWorker;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

/**
 * MainActivity class for the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the Firebase messaging worker process
        startFirebaseMessagingWorkerProcess();

        // Start LoginActivity
        launchLoginActivity();
    }

    /**
     * Starts the Firebase Messaging Worker process to handle periodic work.
     */
    private void startFirebaseMessagingWorkerProcess() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Handle unsuccessful token retrieval
                        return;
                    }
                    String token = task.getResult();
                    scheduleFirebaseMessagingWorker(token);
                });
    }

    /**
     * Schedules a periodic worker for Firebase Messaging.
     * @param token The Firebase Messaging token.
     */
    private void scheduleFirebaseMessagingWorker(String token) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.HOURS)
                .setInputData(new Data.Builder().putString("token", token).build())
                .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork("firebaseMessagingWorker", ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }

    /**
     * Launches the LoginActivity.
     */
    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
