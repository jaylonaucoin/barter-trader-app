package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR_API_KEY");
        }
        placesClient = Places.createClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Get info about the selected place.
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    // Handle the error.
                    Log.i("PlacesApi", "An error occurred: " + status);
                }
            });
        }

        Button confirmLocationButton = findViewById(R.id.confirm_location_button);
        confirmLocationButton.setOnClickListener(v -> {
            LatLng currentLatLng = mMap.getCameraPosition().target;
            saveLocationToRealtimeDatabase(currentLatLng);
        });
    }

    private void saveLocationToRealtimeDatabase(LatLng latLng) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Store address under the user's ID
        DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");

        Map<String, Object> address = new HashMap<>();
        address.put("latitude", latLng.latitude);
        address.put("longitude", latLng.longitude);

        userAddressRef.push().setValue(address)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("address")) {
            String address = intent.getStringExtra("address");

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(RectangularBounds.newInstance(
                            new LatLng(-85, -180),
                            new LatLng(85, 180)))
                    .setOrigin(new LatLng(56.1304, -106.3468))
                    .setCountries("CA")
                    .setQuery(address)
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                if (response.getAutocompletePredictions().isEmpty()) {
                    Toast.makeText(this, "Address not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                String placeId = prediction.getPlaceId();

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, Collections.singletonList(Place.Field.LAT_LNG)).build();

                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((placeResponse) -> {
                    Place place = placeResponse.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Log.e("PlacesApi", "Error fetching place details: " + exception.getMessage());
                    }
                });
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e("PlacesApi", "Error finding address: " + exception.getMessage());
                }
            });
        }
    }
}
