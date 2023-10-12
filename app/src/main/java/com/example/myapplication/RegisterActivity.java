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
        setContentView(R.layout.activity_register);
        connectToFirebase();
        writeToFirebase();
    }

    private void connectToFirebase(){
        firebaseDB = FirebaseDatabase.getInstance();
        userNode = firebaseDB.getReference("user");
    }

    private void writeToFirebase(){
       userNode.child("id").push().setValue("2");
    }
}
