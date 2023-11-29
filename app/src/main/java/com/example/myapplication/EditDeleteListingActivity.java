package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        String listingKey = getIntent().getStringExtra("listingKey");

        // Initialize UI elements
        TextView listingDetailsTextView = findViewById(R.id.listingDetailsTextView);
        EditText editProductName = findViewById(R.id.editProductName);
        EditText editDescription = findViewById(R.id.editDescription);
        Spinner editCategory = findViewById(R.id.editCategory);
        Spinner editCondition = findViewById(R.id.editCondition);
        EditText editExchangePreference = findViewById(R.id.editExchangePreference);
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Extract listing details
        String[] listingDetailsLines = listingDetails.split("\n");
        String productName = listingDetailsLines[1].replace("Product Name: ", "");
        String description = listingDetailsLines[2].replace("Description: ", "");
        String category = listingDetailsLines[3].replace("Category: ", "");
        String condition = listingDetailsLines[4].replace("Condition: ", "");
        String exchangePreference = listingDetailsLines[5].replace("Exchange Preference: ", "");

        // Set initial values for UI elements
        listingDetailsTextView.setText(listingDetails);
        editProductName.setText(productName);
        editDescription.setText(description);

        // Set up category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCategory.setAdapter(categoryAdapter);
        int categoryPosition = categoryAdapter.getPosition(category);
        editCategory.setSelection(categoryPosition);

        // Set up the condition spinner
        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(this, R.array.condition_array, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCondition.setAdapter(conditionAdapter);
        int conditionPosition = conditionAdapter.getPosition(condition);
        editCondition.setSelection(conditionPosition);


        editExchangePreference.setText(exchangePreference);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSaveButtonClicked(listingKey, productName, description, category, condition, exchangePreference);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteButtonClicked(listingKey);
            }
        });
    }

    private void connectToFirebase() {
        // Initialize Firebase instances
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
        auth = FirebaseAuth.getInstance();
    }

    private void handleSaveButtonClicked(String listingKey, String productName, String description, String category, String condition, String exchangePreference) {
        // Get the updated data from UI elements
        EditText editProductName = findViewById(R.id.editProductName);
        EditText editDescription = findViewById(R.id.editDescription);
        Spinner editCategory = findViewById(R.id.editCategory);
        Spinner editCondition = findViewById(R.id.editCondition);
        EditText editExchangePreference = findViewById(R.id.editExchangePreference);

        String updatedProductName = editProductName.getText().toString();
        String updatedDescription = editDescription.getText().toString();
        String updatedCategory = editCategory.getSelectedItem().toString();
        String updatedCondition = editCondition.getSelectedItem().toString();
        String updatedExchangePreference = editExchangePreference.getText().toString();

        // Check if any of the fields have changed
        boolean hasChanges = !updatedProductName.equals(productName) ||
                !updatedDescription.equals(description) ||
                !updatedCategory.equals(category) ||
                !updatedCondition.equals(condition) ||
                !updatedExchangePreference.equals(exchangePreference);

        if (hasChanges) {
            // Update the database with the new data
            DatabaseReference listingRef = firebaseDBRef.child(listingKey);

            // Update the database fields with the new data
            listingRef.child("Product Name").setValue(updatedProductName);
            listingRef.child("Description").setValue(updatedDescription);
            listingRef.child("Category").setValue(updatedCategory);
            listingRef.child("Condition").setValue(updatedCondition);
            listingRef.child("Exchange Preference").setValue(updatedExchangePreference);

            // Notify the user that changes have been saved
            Toast.makeText(EditDeleteListingActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
        } else {
            // No changes were made
            Toast.makeText(EditDeleteListingActivity.this, "No changes detected", Toast.LENGTH_SHORT).show();
        }

        // Return to the UserListingActivity
        Intent returnIntent = new Intent(EditDeleteListingActivity.this, UserListingActivity.class);
        startActivity(returnIntent);
    }

    private void handleDeleteButtonClicked(String listingKey) {
        // Show a confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditDeleteListingActivity.this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this listing?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the listing using the listingKey
                firebaseDBRef.child(listingKey).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            // Deletion was successful
                            finish();
                        } else {
                            // Notify the user that listing could not be deleted
                            Toast.makeText(EditDeleteListingActivity.this, "Deletion was unsuccessful", Toast.LENGTH_SHORT).show();
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
}
