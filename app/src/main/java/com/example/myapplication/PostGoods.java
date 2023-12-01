package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostGoods extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_goods);
        connectToDBase(); // Connect to Firebase database

        // Get references to UI elements
        Button submitButton = findViewById(R.id.submit_button);

        // Toast messages for success and failure
        Toast successToast = Toast.makeText(getApplicationContext(), "Item uploaded successfully", Toast.LENGTH_SHORT);
        Toast failToast = Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT);

        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get values from UI elements
                EditText prodName = findViewById(R.id.name);
                Spinner category = findViewById(R.id.category);
                Spinner condition = findViewById(R.id.condition);
                EditText description = findViewById(R.id.description);
                EditText preference = findViewById(R.id.preference);

                // Extract string values
                String prodValue = prodName.getText().toString().trim();
                String prodCategory = category.getSelectedItem().toString().trim();
                String conditionValue = condition.getSelectedItem().toString().trim();
                String descriptionValue = description.getText().toString().trim();
                String preferenceValue = preference.getText().toString().trim();

                // Check if all fields are filled
                if (!prodValue.isEmpty() && !prodCategory.isEmpty() && !conditionValue.isEmpty() && !descriptionValue.isEmpty() && !preferenceValue.isEmpty()) {
                    successToast.show(); // Show success toast
                    writeToFireDB(prodValue, prodCategory, conditionValue, descriptionValue, preferenceValue); // Write data to Firebase DB
                    Intent searchIntent = new Intent(PostGoods.this, SearchActivity.class);
                    startActivity(searchIntent); // Start SearchActivity
                } else {
                    failToast.show(); // Show failure toast if fields are not filled
                }
            }
        });
    }

    // Connect to Firebase database
    private void connectToDBase() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
        auth = FirebaseAuth.getInstance();
    }

    // Write data to Firebase database
    private void writeToFireDB(String name, String category, String condition, String description, String preference) {
        String id = firebaseDBRef.push().getKey(); // Generate unique ID for the entry
        String uid = auth.getCurrentUser().getUid(); // Get current user ID

        // Get references for user and address information
        DatabaseReference userRef = firebaseDB.getReference("User").child(uid);
        DatabaseReference addressRef = userRef.child("addresses").child("0");
        firebaseDBRef = firebaseDB.getReference("Listings/" + id);

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