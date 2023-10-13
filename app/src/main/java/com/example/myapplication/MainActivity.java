package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    // temporary change main activity to user profile for testing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference firebaseDBRef = firebaseDB.getReference("test");
        firebaseDBRef.setValue("Hello World");

        Intent intent = new Intent(MainActivity.this, UserProfile.class);
        startActivity(intent);
    }
}
