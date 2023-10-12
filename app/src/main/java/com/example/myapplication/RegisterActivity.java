package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference userNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToFirebase();
    }

    private void connectToFirebase(){
        firebaseDB = FirebaseDatabase.getInstance("https://barter-app-50729-default-rtdb.firebaseio.com/");
        userNode = firebaseDB.getReference("Users");
    }

    private void writeToFirebase(){
        userNode.setValue("Hello World");
    }
}
