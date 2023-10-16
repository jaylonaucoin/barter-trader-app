package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToFirebase();
        writeToFirebase();
        Button toPreference =findViewById(R.id.button);
        toPreference.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserPreference.class);
            startActivity(intent);
        });
    }


    private void connectToFirebase(){
        firebaseDB = FirebaseDatabase.getInstance("https://barter-app-50729-default-rtdb.firebaseio.com/");
        firebaseDBRef = firebaseDB.getReference("test");
    }

    private void writeToFirebase(){
        firebaseDBRef.setValue("Hello World");
    }
}

