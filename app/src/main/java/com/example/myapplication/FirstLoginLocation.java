package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class FirstLoginLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login_location);

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDqCJ4dSQzQX8lSTyIva4hWX-7lqqVFS_Y");
        }

        // Setup Autocomplete Support Fragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        // Specify the types of place data to return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(fields);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place
                Log.i("PlacesApi", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                // Start MapsActivity and pass the selected address
                Intent intent = new Intent(FirstLoginLocation.this, MapsActivity.class);
                intent.putExtra("address", place.getName());
                intent.putExtra("location", place.getLatLng());
                startActivity(intent);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("PlacesApi", "An error occurred: " + status);
            }
        });
    }
}
