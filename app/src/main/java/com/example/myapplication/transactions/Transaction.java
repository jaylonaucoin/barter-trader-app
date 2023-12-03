package com.example.myapplication.transactions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.messaging.Message;
import com.example.myapplication.messaging.MessagingActivity;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Transaction extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference dbReference;
    private FirebaseAuth auth;
    Button requestButton, cancelButton;

    String userId = "8VZgWEx1PYgn9zdfqkHhPloCsop1"; // Hardcoded
    String receiverId = "8VZgWEx1PYgn9zdfqkHhPloCsop1"; //Hardcoded
    MessagingActivity messagingActivity;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_transaction_request);

        requestButton = (Button) findViewById(R.id.requestButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setVisibility(View.GONE);

        Toast requestSent = Toast.makeText(getApplicationContext(), "Exchange request has been sent", Toast.LENGTH_SHORT);
        Toast requestCancelled = Toast.makeText(getApplicationContext(), "Exchange request has been cancelled", Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        String selectedItem = intent.getStringExtra("selectedItem");

        // receiving the product name
        if (selectedItem != null) {
            // Split the selectedItem to get product name and seller name
            String[] parts = selectedItem.split("<br>");
            if (parts.length >= 1) {
                String productName = parts[0];

                //on clicking the request button
                requestButton.setOnClickListener(v -> {
                    // Create a message indicating a request for the product
                    Message requestMessage = new Message(userId, receiverId, "I'm interested in " + productName, System.currentTimeMillis());
                    // Push this request message to the seller's chat path
                    messagingActivity.sendRequest(requestMessage);
                    requestSent.show();
                });


                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestCancelled.show();
                        cancelButton.setVisibility(View.GONE);
                        requestButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}

