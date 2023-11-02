package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EditDeleteListingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        // Retrieve listing details from the intent
        String listingDetails = getIntent().getStringExtra("listingDetails");

        // Populate the UI fields with the listing details
        TextView editListingDetailsTextView = findViewById(R.id.editProductName);
        editListingDetailsTextView.setText(listingDetails);

        // Implement the edit and delete functionality
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the edit action
                // You can launch an edit activity or perform any other action here
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the delete action
                // You can show a confirmation dialog or directly delete the listing
            }
        });
    }
}
