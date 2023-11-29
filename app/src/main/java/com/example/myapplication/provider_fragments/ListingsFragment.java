package com.example.myapplication.provider_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
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
        adapter = new ListingsAdapter();
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

    static class Listing {
        public String condition;
        public String exchangePreference;
        public String description;
        public String productName;

        public Listing(String condition, String exchangePreference, String description, String productName) {
            this.condition = condition;
            this.exchangePreference = exchangePreference;
            this.description = description;
            this.productName = productName;
        }
    }

    static class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ListingViewHolder> {
        private List<Listing> listings = new ArrayList<>();

        public void setListings(List<Listing> listings) {
            this.listings = listings;
            notifyDataSetChanged(); // Notify the adapter that data has changed
        }

        @NonNull
        @Override
        public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_item, parent, false);
            return new ListingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
            Listing listing = listings.get(position);
            holder.conditionView.setText(listing.condition);
            holder.exchangePreferenceView.setText(listing.exchangePreference);
            holder.descriptionView.setText(listing.description);
            holder.productNameView.setText(listing.productName);
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

            public ListingViewHolder(View itemView) {
                super(itemView);
                conditionView = itemView.findViewById(R.id.conditionTextView);
                exchangePreferenceView = itemView.findViewById(R.id.exchangePreferenceTextView);
                descriptionView = itemView.findViewById(R.id.descriptionTextView);
                productNameView = itemView.findViewById(R.id.productNameTextView);
            }
        }
    }
}
