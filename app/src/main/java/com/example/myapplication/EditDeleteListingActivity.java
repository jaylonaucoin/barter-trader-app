package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditDeleteListingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        // Retrieve listing details from the intent
        String listingDetails = getIntent().getStringExtra("listingDetails");

        // Get the TextView within the CardView
        TextView listingDetailsTextView = findViewById(R.id.listingDetailsTextView);

        // Set the listing details text
        listingDetailsTextView.setText(listingDetails);

        // Implement the edit and delete functionality
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
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
