package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// Activity to handle first login location functionality
public class FirstLoginLocation extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private DatabaseReference mUserAddressesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login_location);

        // Initialize Firebase auth and database references
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mUserAddressesRef = mDatabase.getReference("User").child(userId).child("addresses");

        // Check if the user's 0th address already exists
        checkIf0thAddressExists();
        // Setup click listener for the location icon
        setupLocationIcon();
        // Initialize Places API
        initializePlaces();
        // Setup the autocomplete fragment
        setupAutocompleteSupportFragment();
    }

    // Checks if the user already has an address set
    private void checkIf0thAddressExists() {
        // Single-value event listener for 0th address
        mUserAddressesRef.child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if it exists, close the activity
                if (dataSnapshot.exists()) {
                    Intent successPage = new Intent(FirstLoginLocation.this, SuccessActivity.class);
                    startActivity(successPage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log database errors
                Log.e("Firebase", "Database error occurred", databaseError.toException());
            }
        });
    }


    // Setup click listener for location icon
    private void setupLocationIcon() {
        // Get the location icon view from the layout
        ImageView locationIcon = findViewById(R.id.automatic_location_icon);
        // Set an onclick listener
        locationIcon.setOnClickListener(view -> {
            // Check for location permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Get current location if permission is granted
                getCurrentLocation();
            }
        });
    }

    // Initialize Google Places API
    private void initializePlaces() {
        // Check if the Places API is already initialized
        if (!Places.isInitialized()) {
            // Initialize with the API key from resources
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
    }

    // Setup autocomplete support fragment for location searching
    private void setupAutocompleteSupportFragment() {
        // Find the fragment by ID and cast it to AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(fields);
            // Set the listener for place selection events
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Handle the selected place
                    Log.i("PlacesApi", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                    // Start MapsActivity with the selected place
                    startMapsActivityWithPlace(place);
                }

                @Override
                public void onError(@NonNull Status status) {
                    // Log any errors encountered
                    Log.e("PlacesApi", "An error occurred: " + status);
                }
            });
        }
    }

    // Retrieve the current location of the device
    private void getCurrentLocation() {
        // Get the system's location service
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            try {
                // Get the last known location from the GPS provider
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    // Start MapsActivity with the current location
                    startMapsActivityWithLocation(location);
                }
            } catch (SecurityException e) {
                // Log any SecurityException that might occur
                Log.e("LocationError", "SecurityException", e);
            }
        }
    }

    // Start MapsActivity with a given Location object
    private void startMapsActivityWithLocation(Location location) {
        Intent intent = new Intent(FirstLoginLocation.this, MapsActivity.class);
        // Pass the location to MapsActivity
        intent.putExtra("location", location);
        startActivity(intent);
    }

    // Start MapsActivity with a given Place object
    private void startMapsActivityWithPlace(Place place) {
        Intent intent = new Intent(FirstLoginLocation.this, MapsActivity.class);
        // Pass the address and location to MapsActivity
        intent.putExtra("address", place.getName());
        intent.putExtra("location", place.getLatLng());
        startActivity(intent);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}
