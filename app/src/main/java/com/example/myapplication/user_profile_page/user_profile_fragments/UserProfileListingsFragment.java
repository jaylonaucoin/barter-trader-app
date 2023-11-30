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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfileListingsFragment extends Fragment {

    private ListingsAdapter adapter;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_listings, container, false);

        RecyclerView rvListings = view.findViewById(R.id.rvUserProfileListings);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListingsAdapter();
        rvListings.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Listings");
        loadUserListings();

        return view;
    }

    private void loadUserListings() {
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Listing> userListingList = new ArrayList<>();
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    String userId = listingSnapshot.child("User ID").getValue(String.class);
                    if (userId != null && userId.equals(currentUserId)) {
                        Listing listing = new Listing(
                                listingSnapshot.child("Condition").getValue(String.class),
                                listingSnapshot.child("Exchange Preference").getValue(String.class),
                                listingSnapshot.child("Description").getValue(String.class),
                                listingSnapshot.child("Product Name").getValue(String.class)
                        );
                        userListingList.add(listing);
                    }
                }
                adapter.setListings(userListingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
