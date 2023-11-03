package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class EditDeleteListingActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        connectToFirebase();

        // Retrieve listing details from the intent
        String listingDetails = getIntent().getStringExtra("listingDetails");

        // Get the TextView within the CardView
        TextView listingDetailsTextView = findViewById(R.id.listingDetailsTextView);

        // Set the listing details text
        listingDetailsTextView.setText(listingDetails);

        // Implement the edit and delete functionality
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Retrieve listing details from the intent
        String[] listingDetailsLines = listingDetails.split("\n");
        String productName = listingDetailsLines[0].replace("Product Name: ", "");
        String description = listingDetailsLines[1].replace("Description: ", "");
        String condition = listingDetailsLines[2].replace("Condition: ", "");
        String exchangePreference = listingDetailsLines[3].replace("Exchange Preference: ", "");
        String address = listingDetailsLines[4].replace("Address: ", "");

        EditText editProductName = findViewById(R.id.editProductName);
        EditText editDescription = findViewById(R.id.editDescription);
        EditText editCondition = findViewById(R.id.editCondition);
        EditText editExchangePreference = findViewById(R.id.editExchangePreference);
        EditText editAddress = findViewById(R.id.editAddress);

        editProductName.setText(productName);
        editDescription.setText(description);
        editCondition.setText(condition);
        editExchangePreference.setText(exchangePreference);
        editAddress.setText(address);

        String listingId = getIntent().getStringExtra("listingId");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated data from EditText fields
                String updatedProductName = editProductName.getText().toString();
                String updatedDescription = editDescription.getText().toString();
                String updatedCondition = editCondition.getText().toString();
                String updatedExchangePreference = editExchangePreference.getText().toString();
                String updatedAddress = editAddress.getText().toString();

                // Check if any of the fields have changed
                boolean hasChanges = !updatedProductName.equals(productName) ||
                        !updatedDescription.equals(description) ||
                        !updatedCondition.equals(condition) ||
                        !updatedExchangePreference.equals(exchangePreference) ||
                        !updatedAddress.equals(address);

                if (hasChanges) {
                    // Update the database with the new data
                    DatabaseReference listingRef = firebaseDB.getReference("Listings").child(listingId);

                    // Update the database fields with the new data
                    listingRef.child("Product Name").setValue(updatedProductName);
                    listingRef.child("Description").setValue(updatedDescription);
                    listingRef.child("Condition").setValue(updatedCondition);
                    listingRef.child("Exchange Preference").setValue(updatedExchangePreference);
                    listingRef.child("Address").setValue(updatedAddress);

                    // Notify the user that changes have been saved
                    Toast.makeText(EditDeleteListingActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();

                    // Return to the UserListingActivity
                    Intent returnIntent = new Intent(EditDeleteListingActivity.this, UserListingActivity.class);
                    startActivity(returnIntent);
                } else {
                    // No changes were made
                    Toast.makeText(EditDeleteListingActivity.this, "No changes detected", Toast.LENGTH_SHORT).show();

                    // Return to the UserListingActivity
                    Intent returnIntent = new Intent(EditDeleteListingActivity.this, UserListingActivity.class);
                    startActivity(returnIntent);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDeleteListingActivity.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this listing?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the listingId from the intent
                        String listingId = getIntent().getStringExtra("listingId");

                        // Delete the listing using the listingId
                        firebaseDBRef.child(listingId).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    // Deletion was successful
                                    finish();
                                } else {
                                    // Deletion failed, handle the error (e.g., show an error message)
                                    // You can also log the error using databaseError.toException()
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog if the user clicks "No"
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void connectToFirebase() {
        // Initialize Firebase instances
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
        auth = FirebaseAuth.getInstance();
    }
}
