package com.example.myapplication.receiver_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReceiverListingFragment extends Fragment {
    private ListView resultsList;
    private DatabaseReference listingNode;
    private List<String> resultsData;
    private ArrayAdapter<String> adapter;
    private final HashMap<String, String> uidMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the receiver_listing.xml layout for this fragment
        return inflater.inflate(R.layout.receiver_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        initializeFirebase();

        // Initialize UI elements
        initializeUIElements(view);

        // Query Firebase for results
        queryFirebaseForResults();
    }

    // Initialize UI elements such as ListView and click listener
    private void initializeUIElements(View view) {
        resultsList = view.findViewById(R.id.resultsListView);

        resultsData = new ArrayList<>();
        adapter = createListAdapter();
        resultsList.setAdapter(adapter);

        // Set click listener for ListView items
        resultsList.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) resultsList.getItemAtPosition(position);
            String sellerUid;

            // Extract seller information from the selected item
            int sellerIndex = selectedItem.lastIndexOf("Seller: ");
            if (sellerIndex != -1) {
                String sellerInfo = selectedItem.substring(sellerIndex);
                String[] sellerString = sellerInfo.split(":");
                if (sellerString.length > 1) {
                    String sellerName = sellerString[1].trim().split("\n")[0]; // Splitting in case there are line breaks after the name
                    sellerUid = uidMap.get(sellerName);

                    // Start the UserProfile activity with the seller's UID
                    Intent intent = new Intent(getActivity(), UserProfile.class);
                    intent.putExtra("uid", sellerUid);
                    startActivity(intent);
                }
            }
        });
    }

    // Create and set up the ArrayAdapter for the ListView
    private ArrayAdapter<String> createListAdapter() {
        return new ArrayAdapter<String>(requireActivity(), R.layout.list_item, resultsData) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = createOrReuseView(convertView, parent);
                TextView nameTextView = view.findViewById(R.id.nameTextView);
                TextView detailsTextView = view.findViewById(R.id.detailsTextView);
                String item = resultsData.get(position);
                displayItemDetails(nameTextView, detailsTextView, item);
                return view;
            }
        };
    }

    // Create a new view or reuse an existing one
    private View createOrReuseView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = getLayoutInflater();
            return inflater.inflate(R.layout.list_item, parent, false);
        }
        return convertView;
    }

    // Display item details in the ListView
    private void displayItemDetails(TextView nameTextView, TextView detailsTextView, String item) {
        String[] parts = item.split("<br>");
        if (parts.length >= 1) {
            String name = parts[0];
            String details = (parts.length > 1) ? parts[1] : "";
            Spannable spannable = makeNameTextBold(name);
            nameTextView.setText(spannable);
            detailsTextView.setText(details);
        } else {
            nameTextView.setText(item);
            detailsTextView.setText("");
        }
    }

    // Make the name text bold using Spannable
    private Spannable makeNameTextBold(String name) {
        Spannable spannable = new SpannableString(name);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    // Query Firebase for results
    private void queryFirebaseForResults() {
        listingNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processResults(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    // Process the results retrieved from Firebase
    private void processResults(DataSnapshot dataSnapshot) {
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            // Extract item details from the DataSnapshot
            String itemName = itemSnapshot.child("Product Name").getValue(String.class);
            String itemCondition = itemSnapshot.child("Condition").getValue(String.class);
            String itemDescription = itemSnapshot.child("Description").getValue(String.class);
            String itemExchangePref = itemSnapshot.child("Exchange Preference").getValue(String.class);
            String itemCategory = itemSnapshot.child("Category").getValue(String.class);
            String seller = itemSnapshot.child("Seller").getValue(String.class);
            String address = itemSnapshot.child("Address").getValue(String.class);
            String sellerUid = itemSnapshot.child("User ID").getValue(String.class);

            // Add the item to the results list and store seller information in the UID map
            addItemToResults(itemName, itemCondition, itemDescription, itemExchangePref, itemCategory, seller, address);
            uidMap.put(seller, sellerUid);
        }
        // Display the search results
        displaySearchResults();
    }

    // Add an item to the results list
    private void addItemToResults(String itemName, String itemCondition, String itemDescription, String itemExchangePref, String itemCategory, String seller, String address) {
        String item = itemName + "<br>" + "Condition: " + itemCondition + "\n" +
                "Description: " + itemDescription + "\n" + "Exchange Preference: " + itemExchangePref + "\n" +
                "Category: " + itemCategory + "\n" + "Address: " + address + "\n" + "Seller: " + seller;
        resultsData.add(item);
    }

    // Update the ListView to display search results
    private void displaySearchResults() {
        adapter.notifyDataSetChanged();
    }

    // Handle database error by printing the error message
    private void handleDatabaseError(DatabaseError databaseError) {
        System.out.println(databaseError.getMessage());
    }

    // Initialize Firebase and set up a reference to the "Listings" node
    private void initializeFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        listingNode = database.getReference("Listings");
    }
}
