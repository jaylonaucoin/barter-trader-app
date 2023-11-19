package com.example.myapplication;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private ListView resultsList;
    private DatabaseReference listingNode;
    private List<String> resultsData;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initializeUIElements();
        initializeFirebase();
        queryFirebaseForResults();
    }

    private void initializeUIElements() {
        resultsList = findViewById(R.id.resultsListView);

        resultsData = new ArrayList<>();
        adapter = createListAdapter();
        resultsList.setAdapter(adapter);
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
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void clearResults() {
        resultsData.clear();
        adapter.notifyDataSetChanged();
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

            addItemToResults(itemName, itemCondition, itemDescription, itemExchangePref, seller, address);
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
        // Handle database error
    }

    private void initializeFirebase() {
        listingNode = FirebaseDatabase.getInstance().getReference("Listings");
    }
}
