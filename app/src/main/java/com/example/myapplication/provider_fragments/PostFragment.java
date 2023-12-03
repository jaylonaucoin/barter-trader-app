package com.example.myapplication.provider_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PostFragment extends Fragment {

    // Firebase instances
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;

    // Listener for post interaction
    private OnPostInteractionListener mListener;

    // Interface for callback on post completion
    public interface OnPostInteractionListener {
        void onPostCompleted();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure that the context implements OnPostInteractionListener
        if (context instanceof OnPostInteractionListener) {
            mListener = (OnPostInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnPostInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.provider_post, container, false);

        // Initialize Firebase connection
        connectToDBase();

        // Set up UI components
        setupUI(view);

        return view;
    }

    // Method to connect to Firebase database
    private void connectToDBase() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
        auth = FirebaseAuth.getInstance();
    }

    // Method to set up UI components
    private void setupUI(View view) {
        Button submitButton = view.findViewById(R.id.submit_button);
        EditText prodName = view.findViewById(R.id.name);
        Spinner category = view.findViewById(R.id.category);
        Spinner condition = view.findViewById(R.id.condition);
        EditText description = view.findViewById(R.id.description);
        EditText preference = view.findViewById(R.id.preference);

        // Toast messages for different outcomes
        Toast successToast = Toast.makeText(getContext(), "Item uploaded successfully", Toast.LENGTH_SHORT);
        Toast failToast = Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT);

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener(v -> {
            // Extract values from UI components
            String prodValue = prodName.getText().toString().trim();
            String prodCategory = category.getSelectedItem().toString().trim();
            String conditionValue = condition.getSelectedItem().toString().trim();
            String descriptionValue = description.getText().toString().trim();
            String preferenceValue = preference.getText().toString().trim();

            // Check if all fields are filled
            if (!prodValue.isEmpty() && !prodCategory.isEmpty() && !conditionValue.isEmpty() && !descriptionValue.isEmpty() && !preferenceValue.isEmpty()) {
                successToast.show();
                writeToFireDB(prodValue, prodCategory, conditionValue, descriptionValue, preferenceValue);

                // Only call onPostCompleted if all fields are filled
                mListener.onPostCompleted();
            } else
                failToast.show();

        });
    }

    // Method to write data to Firebase database
    private void writeToFireDB(String name, String category, String condition, String description, String preference) {
        String id = firebaseDBRef.push().getKey(); // Generate unique ID for the entry
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid(); // Get current user ID

        // Store user and address information
        storeUserAndAddressInfo(uid, id, name, description, category, condition, preference);
    }

    // Method to store user and address information in Firebase
    private void storeUserAndAddressInfo(String uid, String id, String name, String description, String category, String condition, String preference) {
        DatabaseReference userRef = firebaseDB.getReference("User").child(uid);
        DatabaseReference addressRef = userRef.child("addresses").child("0");
        firebaseDBRef = firebaseDB.getReference("Listings/" + id);

        retrieveAndStoreAddressDetails(addressRef);
        retrieveAndStoreUserDetails(userRef);

        // Set product details in Firebase
        firebaseDBRef.child("User ID").setValue(uid);
        firebaseDBRef.child("Product Name").setValue(name);
        firebaseDBRef.child("Description").setValue(description);
        firebaseDBRef.child("Category").setValue(category);
        firebaseDBRef.child("Condition").setValue(condition);
        firebaseDBRef.child("Exchange Preference").setValue(preference);
    }

    // Retrieve and store address details
    private void retrieveAndStoreAddressDetails(DatabaseReference addressRef) {
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
                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Retrieve and store user's first and last name
    private void retrieveAndStoreUserDetails(DatabaseReference userRef) {
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
                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
