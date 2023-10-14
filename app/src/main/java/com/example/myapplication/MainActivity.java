package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToFirebase();

        // grabbing the login button
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

    private void connectToFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("test");
    }
}