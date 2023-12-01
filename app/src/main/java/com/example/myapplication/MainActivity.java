package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import com.example.myapplication.notifications.NotificationWorker;
import com.example.myapplication.registration.LoginActivity;
import com.example.myapplication.registration.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(NotificationWorker.class,1, TimeUnit.HOURS);
        Data data = new Data.Builder()
                .putString("token", token)
                .build();

        PeriodicWorkRequest hourlyWork = workBuilder.setInputData(data).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("test", ExistingPeriodicWorkPolicy.KEEP, hourlyWork);

    }
    public void startWorkerProcess(){
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


