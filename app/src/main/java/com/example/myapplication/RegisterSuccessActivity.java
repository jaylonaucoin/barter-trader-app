package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterSuccessActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    String name;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        // Get the user's first name from the previous activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        // Find the welcome message TextView and set it with user's name
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        welcomeMessage.setText("Welcome " + name + "!");

        // Initialize Firebase components
        connectToFirebase();
    }

    private void connectToFirebase(){
        // Initialize Firebase instances
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("User");
        auth = FirebaseAuth.getInstance();
    }
}
