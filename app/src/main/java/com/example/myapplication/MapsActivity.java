package com.example.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import android.location.Address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// This activity handles map interactions using Google Maps and the Google Places API for selecting locations.
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG_PLACES_API = "PlacesApi";

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Firebase authentication and get a reference to the Firebase database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Google Places API
        initializePlaces();

        // Prepare the map fragment by asynchronously obtaining the map
        setupMapFragment();

        // Setup the autocomplete search fragment for Google Places API
        setupAutocompleteFragment();

        // Setup the button which will confirm the selection of a location
        setupConfirmLocationButton();
    }

    // Initialize the Google Places API client with an API key
    private void initializePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key)); // Replace with actual API key
        }
        placesClient = Places.createClient(this);
    }

    // Setup and register the map fragment within the activity
    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Configure the autocomplete fragment for location searching
    private void setupAutocompleteFragment() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            // Set up a PlaceSelectionListener to handle the response when a user selects a place.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Handle the selection by moving the map's camera to the selected place and adding a marker
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    // Handle any errors returned by the Places API
                    Log.i(TAG_PLACES_API, "An error occurred: " + status);
                }
            });
        }
    }

    // Configures the confirm location button to save the currently selected location
    private void setupConfirmLocationButton() {
        Button confirmLocationButton = findViewById(R.id.confirm_location_button);
        confirmLocationButton.setOnClickListener(v -> {
            // Get the current map camera position and save it to Firebase Realtime Database
            LatLng currentLatLng = mMap.getCameraPosition().target;
            saveLocationToRealtimeDatabase(currentLatLng);

            // goes to success page
            Intent intent = new Intent(MapsActivity.this, SuccessActivity.class);
            startActivity(intent);
        });
    }

    // Save the selected location to Firebase Realtime Database
    private void saveLocationToRealtimeDatabase(LatLng latLng) {
        // Use the Geocoder to get an address from the LatLng coordinates
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String addressString = addresses.get(0).getAddressLine(0);

                // Ensure that a user is logged in before attempting to save their address
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get a reference to the user's addresses in the database and save the new address
                String userId = currentUser.getUid();
                DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");

                // Attempt to retrieve the user's addresses and add the new one
                userAddressRef.get().addOnCompleteListener(task -> {
                    long nextIndex = 0;  // Default index if no addresses exist
                    if (task.isSuccessful() && task.getResult() != null) {
                        nextIndex = task.getResult().getChildrenCount();
                    }
                    // Create a map to represent the address data
                    Map<String, Object> address = new HashMap<>();
                    address.put("latitude", latLng.latitude);
                    address.put("longitude", latLng.longitude);
                    address.put("address", addressString);

                    // Save the address to the next index in the user's addresses list
                    userAddressRef.child(String.valueOf(nextIndex)).setValue(address)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Error saving location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                });
            } else {
                Toast.makeText(this, "Unable to get address from the location.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Geocoder failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Closes activity
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Assign the fully initialized Google Map to the mMap variable.
        mMap = googleMap;

        // Create a LatLng object for the city of Sydney.
        LatLng sydney = new LatLng(-34, 151);
        // Add a marker on the map at the location of Sydney.
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // Move the camera view to Sydney with a default zoom level.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Get the Intent that started this activity to check for additional data.
        Intent intent = getIntent();
        // Check if an address was passed to the activity as an extra.
        if (intent != null && intent.hasExtra("address")) {
            String address = intent.getStringExtra("address");

            // Prepare a request for autocomplete predictions with bias to Canada
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(RectangularBounds.newInstance(
                            new LatLng(-85, -180),
                            new LatLng(85, 180)))
                    .setOrigin(new LatLng(56.1304, -106.3468))
                    .setCountries("CA")
                    .setQuery(address)
                    .build();

            // Use the Places Client to find the autocomplete predictions.
            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                // Check if no predictions were found.
                if (response.getAutocompletePredictions().isEmpty()) {
                    Toast.makeText(this, "Address not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the first prediction and retrieve its place ID.
                AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                String placeId = prediction.getPlaceId();

                // Create a request to fetch the place details using the place ID.
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, Collections.singletonList(Place.Field.LAT_LNG)).build();

                // Fetch the place details.
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((placeResponse) -> {
                    Place place = placeResponse.getPlace();
                    LatLng latLng = place.getLatLng();
                    // If the LatLng is not null, move the camera to the place and add a marker.
                    if (latLng != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                    }
                }).addOnFailureListener((exception) -> {
                    // Handle any errors in fetching the place details.
                    if (exception instanceof ApiException) {
                        Log.e(TAG_PLACES_API, "Error fetching place details: " + exception.getMessage());
                    }
                });
            }).addOnFailureListener((exception) -> {
                // Handle errors in finding the address.
                if (exception instanceof ApiException) {
                    Log.e(TAG_PLACES_API, "Error finding address: " + exception.getMessage());
                }
            });
        }
        // Check if a location was passed to the activity as an extra.
        else if (intent != null && intent.hasExtra("location")) {
            Location location = intent.getParcelableExtra("location");
            // If the location extra is not null, move the camera to that location and add a marker.
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            }
        }
    }
}
