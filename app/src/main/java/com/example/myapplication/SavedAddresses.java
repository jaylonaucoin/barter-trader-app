package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// The SavedAddresses activity is responsible for displaying and managing a list of saved addresses for a user.
public class SavedAddresses extends AppCompatActivity implements LocationHelper.LocationCallback {


    // Firebase database reference and the ID of the current user.
    private DatabaseReference mDatabase;
    private String userId;
    private LocationHelper locationHelper;

    // The RecyclerView for displaying the list of saved addresses.
    private RecyclerView recyclerView;

    // A list to hold maps of the address details fetched from Firebase.
    private final List<Map<String, Object>> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_addresses);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        locationHelper = new LocationHelper(this, this);

        // Checks if the user is logged in and exits if not.
        verifyUserLoggedIn();

        // Sets up the custom toolbar with back navigation.
        setupToolbar();

        // Initialize Firebase references and user ID, and fetch addresses from the database.
        initializeDatabaseAndUserId();

        // Initialize RecyclerView.
        setupRecyclerView();

        // Set up the autocomplete fragment for address search.
        setupAutocompleteSupportFragment();

        // Set up the location icon for fetching the current location.
        setupLocationIcon();
    }

    // Verifies if the user is logged in and exits if not
    private void verifyUserLoggedIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showToastAndFinish();
        }
    }

    // Initializes Firebase database references and gets the current user ID
    private void initializeDatabaseAndUserId() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fetchAddresses();
    }

    // Sets up the toolbar with back navigation
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.savedaddress_page_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tintToolbarNavigationIcon(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    // Fetches saved addresses from Firebase and updates the RecyclerView
    private void fetchAddresses() {
        DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");
        userAddressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                populateAddressList(dataSnapshot);
                if (recyclerView.getAdapter() != null)
                    recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read addresses", databaseError.toException());
            }
        });
    }

    // Parses and populates the address list from the Firebase snapshot
    private void populateAddressList(DataSnapshot dataSnapshot) {
        addressList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> address = (Map<String, Object>) snapshot.getValue();
            if (address != null) {
                addressList.add(address);
            }
        }
    }

    // Sets up the AutocompleteSupportFragment for location search
    private void setupAutocompleteSupportFragment() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(fields);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("PlacesApi", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                Intent intent = new Intent(SavedAddresses.this, MapsActivity.class);
                intent.putExtra("address", place.getName());
                intent.putExtra("location", place.getLatLng());
                intent.putExtra("sourceActivity", "SavedAddresses");
                startActivity(intent);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("PlacesApi", "An error occurred: " + status);
            }
        });
    }

    // Sets up the location icon for fetching current location
    private void setupLocationIcon() {
        ImageView locationIcon = findViewById(R.id.automatic_location_icon);
        locationIcon.setOnClickListener(v -> getCurrentLocation());
    }

    // Clears the search input from the AutocompleteSupportFragment
    private void clearAutocompleteSearchInput() {

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null && autocompleteFragment.getView() != null) {
            EditText editText = autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);
            if (editText != null) {
                editText.setText("");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, grantResults);
    }

    // Sends location to MapsActivity
    @Override
    public void onLocationRetrieved(Location location) {
        startMapsActivity(location);
    }

    // Gets Location using FusedLocationProviderClient
    private void getCurrentLocation() {
        locationHelper.getCurrentLocation();
    }


    // Starts the MapsActivity with the given location
    private void startMapsActivity(Location location) {
        Intent intent = new Intent(SavedAddresses.this, MapsActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("sourceActivity", "SavedAddresses");
        startActivity(intent);
    }

    // Sets up the RecyclerView with the AddressAdapter
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.address_recycler);
        recyclerView.setAdapter(new AddressAdapter(addressList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Helper method to tint the toolbar navigation icon
    private void tintToolbarNavigationIcon(Toolbar toolbar) {
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);
        }
    }

    // Helper method to show a toast and finish the activity
    private void showToastAndFinish() {
        Toast.makeText(this, "Please log in to view saved addresses.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

        private final List<Map<String, Object>> addresses;

        public AddressAdapter(List<Map<String, Object>> addresses) {
            this.addresses = addresses;
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {

            // Clears the search input whenever the recycler gets updated (i.e, when adding a new address)
            clearAutocompleteSearchInput();

            Map<String, Object> address = addresses.get(position);

            // Assuming your address is stored in the 'address' key in your Firebase database
            String addressDetails = Objects.requireNonNull(address.get("address")).toString();
            holder.addressText.setText(addressDetails);

            if(position == 0) {
                holder.itemView.setBackgroundResource(R.color.purple_tint);
                holder.removeIcon.setVisibility(View.INVISIBLE);
            } else {
                holder.itemView.setBackgroundResource(R.color.white);
                holder.removeIcon.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(v -> {
                // Rearrange the addresses such that clicked one becomes the top address
                Map<String, Object> clickedAddress = addresses.remove(position);
                addresses.add(0, clickedAddress);
                notifyDataSetChanged();

                updateAddressesInDatabase();
            });

            holder.removeIcon.setOnClickListener(v -> {
                addresses.remove(position);
                notifyDataSetChanged();

                updateAddressesInDatabase();
            });
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }

        private void updateAddressesInDatabase() {
            DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");
            userAddressRef.setValue(addresses)
                    .addOnSuccessListener(aVoid -> Toast.makeText(SavedAddresses.this, "Address updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(SavedAddresses.this, "Error updating addresses: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        // ViewHolder class for each item in the RecyclerView
        class AddressViewHolder extends RecyclerView.ViewHolder {
            TextView addressText;
            ImageView removeIcon;

            public AddressViewHolder(@NonNull View itemView) {
                super(itemView);
                addressText = itemView.findViewById(R.id.address_text);
                removeIcon = itemView.findViewById(R.id.remove_address_icon);
            }
        }

    }
}
