package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginSuccessActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        connectToFirebase();

        // grabbing the logout button to add an on-click listener
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when logout is clicked
            public void onClick(View v) {
                // signing out the user
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LoginSuccessActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // going back to the login page
                Intent loginIntent = new Intent(LoginSuccessActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
    private void connectToFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("test");
    }
}
