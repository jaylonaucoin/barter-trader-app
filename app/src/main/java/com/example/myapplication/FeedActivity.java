package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedActivity extends AppCompatActivity {
    private DatabaseReference listingNode;
    private List<String> resultsData;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> uidMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initializeFirebase();
        initializeUIElements();
        queryFirebaseForResults();
    }

    private void initializeUIElements() {
        ListView resultsList;
        resultsList = findViewById(R.id.resultsListView);

        resultsData = new ArrayList<>();
        adapter = createListAdapter();
        resultsList.setAdapter(adapter);
        resultsList.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click here
            String selectedItem = (String) resultsList.getItemAtPosition(position);

            String sellerUid;

            String[] parts = selectedItem.split("<br>");
            String[] parts2 = parts[1].split("\n");
            String[] sellerString = parts2[4].split(":");
            String sellerName = sellerString[1].trim();
            sellerUid = uidMap.get(sellerName);

            // Create an intent to start another activity
            Intent intent = new Intent(FeedActivity.this, UserProfile.class);

            // Add any necessary data to the intent using putExtra (if needed)
            intent.putExtra("uid", sellerUid);

            // Start the activity
            startActivity(intent);
        });
    }

    private ArrayAdapter<String> createListAdapter() {
        return new ArrayAdapter<String>(this, R.layout.list_item, resultsData) {
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

    private View createOrReuseView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = getLayoutInflater();
            return inflater.inflate(R.layout.list_item, parent, false);
        }
        return convertView;
    }

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

    private Spannable makeNameTextBold(String name) {
        Spannable spannable = new SpannableString(name);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

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

    private void processResults(DataSnapshot dataSnapshot) {
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            String itemName = itemSnapshot.child("Product Name").getValue(String.class);
            String itemCondition = itemSnapshot.child("Condition").getValue(String.class);
            String itemDescription = itemSnapshot.child("Description").getValue(String.class);
            String itemExchangePref = itemSnapshot.child("Exchange Preference").getValue(String.class);
            String seller = itemSnapshot.child("Seller").getValue(String.class);
            String address = itemSnapshot.child("Address").getValue(String.class);
            String sellerUid = itemSnapshot.child("User ID").getValue(String.class);

            addItemToResults(itemName, itemCondition, itemDescription, itemExchangePref, seller, address);
            uidMap.put(seller, sellerUid);
        }
        displaySearchResults();
    }

    private void addItemToResults(String itemName, String itemCondition, String itemDescription, String itemExchangePref, String seller, String address) {
        String item = itemName + "<br>" + "Condition: " + itemCondition + "\n" +
                "Description: " + itemDescription + "\n" + "Exchange Preference: " + itemExchangePref + "\n" + "Address: "
                + address + "\n" + "Seller: " + seller;
        resultsData.add(item);
    }

    private void displaySearchResults() {
        adapter.notifyDataSetChanged();
    }

    private void handleDatabaseError(DatabaseError databaseError) {

        System.out.println(databaseError.getMessage());
    }

    private void initializeFirebase() {
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        listingNode = database.getReference("Listings");
    }
}
