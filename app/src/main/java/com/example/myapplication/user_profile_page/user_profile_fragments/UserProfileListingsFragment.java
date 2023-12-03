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
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_listings, container, false);
        RecyclerView rvListings = view.findViewById(R.id.rvUserProfileListings);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));

        // Determine if the fragment is displaying the current user's profile
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        // Get the user ID whose listings we need to display
        Bundle args = getArguments();
        String profileUserId = args != null ? args.getString("uid") : currentUserId;

        // Check if the user is viewing their own profile
        boolean isCurrentUserProfile = profileUserId != null && profileUserId.equals(currentUserId);

        adapter = new ListingsAdapter(getContext(), isCurrentUserProfile);
        rvListings.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Listings");
        loadUserListings(profileUserId);

        return view;
    }

    private void loadUserListings(String userId) {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Listing> userListingList = new ArrayList<>();
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    String listingUserId = listingSnapshot.child("User ID").getValue(String.class);
                    if (listingUserId != null && listingUserId.equals(userId)) {
                        Listing listing = new Listing(
                                listingSnapshot.child("Condition").getValue(String.class),
                                listingSnapshot.child("Exchange Preference").getValue(String.class),
                                listingSnapshot.child("Description").getValue(String.class),
                                listingSnapshot.child("Product Name").getValue(String.class),
                                listingSnapshot.child("Category").getValue(String.class),
                                listingSnapshot.getKey()
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
