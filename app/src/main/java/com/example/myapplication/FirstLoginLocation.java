package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


public class FirstLoginLocation extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    PlacesClient placesClient;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_location);

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDqCJ4dSQzQX8lSTyIva4hWX-7lqqVFS_Y");
        }

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = arrayAdapter.getItem(position);
            if (selectedPlace != null) {
                Log.i("PlacesApi", "Place selected: " + selectedPlace);
                // You can use selectedPlace for further actions such as fetching details about the place
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(RectangularBounds.newInstance(
                                new LatLng(-85, -180),
                                new LatLng(85, 180)))
                        .setOrigin(new LatLng(56.1304, -106.3468)) // Canada
                        .setCountries("CA") // Canada
                        .setSessionToken(token)
                        .setQuery(charSequence.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    arrayAdapter.clear();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        arrayAdapter.add(prediction.getFullText(null).toString());
                    }
                    arrayAdapter.notifyDataSetChanged();
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Log.e("PlacesApi", "Error getting suggestions: " + exception.getMessage());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}


