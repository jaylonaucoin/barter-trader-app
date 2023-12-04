package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditDeleteListingActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        connectToFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable navIcon = ContextCompat.getDrawable(this, R.drawable.ic_back);
        if (navIcon != null) {
            navIcon = DrawableCompat.wrap(navIcon);
            DrawableCompat.setTint(navIcon, ContextCompat.getColor(this, android.R.color.white));
            toolbar.setNavigationIcon(navIcon);
        }
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());

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
        assert listingDetails != null;
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


        saveButton.setOnClickListener(v -> handleSaveButtonClicked(listingKey, productName, description, category, condition, exchangePreference));

        deleteButton.setOnClickListener(v -> handleDeleteButtonClicked());
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
        finish();
    }

    private void handleDeleteButtonClicked() {
        // Show a confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditDeleteListingActivity.this);
        builder.setTitle("Confirm Removal");
        builder.setMessage("Are you sure you want to remove this listing?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Retrieve listing details from the intent
            String listingDetails = getIntent().getStringExtra("listingDetails");
            String listingKey1 = getIntent().getStringExtra("listingKey");

            // Extract listing details
            assert listingDetails != null;
            String[] listingDetailsLines = listingDetails.split("\n");
            String productName = listingDetailsLines[1].replace("Product Name: ", "");
            String description = listingDetailsLines[2].replace("Description: ", "");
            String category = listingDetailsLines[3].replace("Category: ", "");
            String condition = listingDetailsLines[4].replace("Condition: ", "");
            String exchangePreference = listingDetailsLines[5].replace("Exchange Preference: ", "");

            // Write the listing to the "Removed Listings" section
            writeToFireDB(productName, category, condition, description, exchangePreference);

            // Remove the listing from the "Listings" section
            DatabaseReference listingsRef = firebaseDB.getReference("Listings");
            DatabaseReference removedListingsRef = firebaseDB.getReference("RemovedListings");

            // Get a reference to the specific listing using its key
            assert listingKey1 != null;
            DatabaseReference specificListingRef = listingsRef.child(listingKey1);

            specificListingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Move the listing to "Removed Listings"
                    removedListingsRef.child(listingKey1).setValue(dataSnapshot.getValue(), (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            // If adding to "Removed Listings" is successful, remove from "Listings"
                            specificListingRef.removeValue((databaseError1, databaseReference1) -> {
                                if (databaseError1 == null) {
                                    // Deletion from "Listings" successful
                                    finish(); // Close the activity or handle UI updates as needed
                                } else {
                                    // Notify the user that listing could not be deleted
                                    Toast.makeText(EditDeleteListingActivity.this, "Removal from Listings was unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Notify the user that adding to "Removed Listings" was unsuccessful
                            Toast.makeText(EditDeleteListingActivity.this, "Adding to Removed Listings was unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error if any
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) ->
                // Dismiss the dialog if the user clicks "No"
                dialog.dismiss());
        builder.show();
    }

    // Write data to Firebase database
    private void writeToFireDB(String name, String category, String condition, String description, String preference) {
        String id = firebaseDBRef.push().getKey(); // Generate unique ID for the entry
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid(); // Get current user ID

        // Get references for user and address information
        DatabaseReference userRef = firebaseDB.getReference("User").child(uid);
        DatabaseReference addressRef = userRef.child("addresses").child("0");
        firebaseDBRef = firebaseDB.getReference("Removed Listings/" + id);

        // Retrieve address details
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Extract latitude, longitude, and address
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);
                String address = snapshot.child("address").getValue(String.class);

                // Set values in Firebase for address details
                firebaseDBRef.child("Address").setValue(address);
                firebaseDBRef.child("Latitude").setValue(latitude);
                firebaseDBRef.child("Longitude").setValue(longitude);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if any
            }
        });

        // Retrieve user's first and last name
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Extract first and last name
                String firstName = snapshot.child("firstName").getValue(String.class);
                String lastName = snapshot.child("lastName").getValue(String.class);

                // Set seller's name in Firebase
                firebaseDBRef.child("Seller").setValue(firstName + " " + lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if any
            }
        });

        // Set product details in Firebase
        firebaseDBRef.child("User ID").setValue(uid);
        firebaseDBRef.child("Product Name").setValue(name);
        firebaseDBRef.child("Description").setValue(description);
        firebaseDBRef.child("Category").setValue(category);
        firebaseDBRef.child("Condition").setValue(condition);
        firebaseDBRef.child("Exchange Preference").setValue(preference);
    }
}
