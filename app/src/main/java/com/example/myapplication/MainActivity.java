package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.user_profile_page.UserProfile;


public class MainActivity extends AppCompatActivity {

    // temporary change main activity to user profile for testing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, UserProfile.class);
        startActivity(intent);
    }
}
