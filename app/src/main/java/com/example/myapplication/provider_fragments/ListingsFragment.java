package com.example.myapplication.provider_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EditDeleteListingActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListingsFragment extends Fragment {

    private ListingsAdapter adapter;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.provider_listings, container, false);
        RecyclerView rvListings = view.findViewById(R.id.rvListings);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // This is the current logged-in user's ID
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        // Assuming you're passing the user ID of the profile being viewed as an argument to the fragment
        // If no user ID is passed, default to the current user ID
        Bundle args = getArguments();
        String profileUserId = args != null ? args.getString("uid") : currentUserId;

        // Determine if the profile being viewed belongs to the current user
        boolean isCurrentUserProfile = profileUserId != null && profileUserId.equals(currentUserId);

        // Initialize the adapter with the context and whether the items should be clickable
        adapter = new ListingsAdapter(getContext(), isCurrentUserProfile);
        rvListings.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Listings");

        loadUserListings();

        return view;
    }

    private void loadUserListings() {
        String currentUserId =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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

    // Update Listing class to include category
    public static class Listing {
        public String condition;
        public String exchangePreference;
        public String description;
        public String productName;
        public String category;
        public String listingKey;

        public Listing(String condition, String exchangePreference, String description,
                       String productName, String category, String listingKey) {
            this.condition = condition;
            this.exchangePreference = exchangePreference;
            this.description = description;
            this.productName = productName;
            this.category = category;
            this.listingKey = listingKey;
        }
    }

    public static class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ListingViewHolder> {
        private List<Listing> listings = new ArrayList<>();
        private final LayoutInflater inflater;
        private final boolean isItemClickable;


        public ListingsAdapter(Context context, boolean isItemClickable) {
            inflater = LayoutInflater.from(context);
            this.isItemClickable = isItemClickable;
        }

        public void setListings(List<Listing> listings) {
            this.listings = listings;
            notifyDataSetChanged(); // Notify the adapter that data has changed
        }

        @NonNull
        @Override
        public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.listing_item, parent, false);
            return new ListingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
            Listing listing = listings.get(position);
            holder.bind(listing, isItemClickable);
        }

        @Override
        public int getItemCount() {
            return listings.size();
        }

        static class ListingViewHolder extends RecyclerView.ViewHolder {
            TextView conditionView;
            TextView exchangePreferenceView;
            TextView descriptionView;
            TextView productNameView;
            TextView categoryView;

            public ListingViewHolder(View itemView) {
                super(itemView);
                conditionView = itemView.findViewById(R.id.conditionTextView);
                exchangePreferenceView = itemView.findViewById(R.id.exchangePreferenceTextView);
                descriptionView = itemView.findViewById(R.id.descriptionTextView);
                productNameView = itemView.findViewById(R.id.productNameTextView);
                categoryView = itemView.findViewById(R.id.categoryTextView);
            }

            public void bind(final Listing listing, boolean isItemClickable) {
                conditionView.setText(listing.condition);
                exchangePreferenceView.setText(listing.exchangePreference);
                descriptionView.setText(listing.description);
                productNameView.setText(listing.productName);
                categoryView.setText(listing.category);

                if (isItemClickable) {
                    itemView.setOnClickListener(v -> {
                        Context context = itemView.getContext();
                        Intent intent = new Intent(context, EditDeleteListingActivity.class);
                        intent.putExtra("listingDetails", createListingDetailsString(listing));
                        intent.putExtra("listingKey", listing.listingKey);
                        context.startActivity(intent);
                    });
                } else {
                    itemView.setOnClickListener(null);
                }
            }

            private String createListingDetailsString(Listing listing) {
                return listing.listingKey + "\n" +
                        "Product Name: " + listing.productName + "\n" +
                        "Description: " + listing.description + "\n" +
                        "Category: " + listing.category + "\n" +
                        "Condition: " + listing.condition + "\n" +
                        "Exchange Preference: " + listing.exchangePreference + "\n";
            }
        }
    }
}
