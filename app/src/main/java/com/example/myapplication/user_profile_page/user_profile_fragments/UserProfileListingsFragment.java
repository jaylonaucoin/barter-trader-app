package com.example.myapplication.user_profile_page.user_profile_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.provider_fragments.ListingsFragment.Listing;
import com.example.myapplication.provider_fragments.ListingsFragment.ListingsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UserProfileListingsFragment extends Fragment {

    private ListingsAdapter adapter;
    private DatabaseReference listingsDatabaseRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile_listings, container, false);
        setupRecyclerView(view);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        Bundle args = getArguments();
        String profileUserId = args != null ? args.getString("uid") : currentUserId;

        boolean isCurrentUserProfile = profileUserId != null && profileUserId.equals(currentUserId);

        // Initialize RecyclerView adapter
        adapter = new ListingsAdapter(getContext(), isCurrentUserProfile);

        // Set up Firebase database reference
        listingsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Listings");
        loadUserListings(profileUserId);

        return view;
    }

    private void setupRecyclerView(View view) {
        // Set up the RecyclerView for displaying listings
        RecyclerView rvListings = view.findViewById(R.id.rvUserProfileListings);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListings.setAdapter(adapter);
    }

    private void loadUserListings(String userId) {
        // Load listings from Firebase database
        listingsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Listing> userListingList = new ArrayList<>();
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    String listingUserId = listingSnapshot.child("User ID").getValue(String.class);
                    if (listingUserId != null && listingUserId.equals(userId)) {
                        userListingList.add(createListingFromSnapshot(listingSnapshot));
                    }
                }
                adapter.setListings(userListingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible database errors
            }
        });
    }

    private Listing createListingFromSnapshot(DataSnapshot snapshot) {
        // Create a Listing object from a DataSnapshot
        return new Listing(
                snapshot.child("Condition").getValue(String.class),
                snapshot.child("Exchange Preference").getValue(String.class),
                snapshot.child("Description").getValue(String.class),
                snapshot.child("Product Name").getValue(String.class),
                snapshot.child("Category").getValue(String.class),
                snapshot.getKey()
        );
    }
}
