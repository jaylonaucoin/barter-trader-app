package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.transactions.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText nameEditText;
    private Spinner conditionSpinner;
    private EditText exchangePreferenceEditText;
    private Button searchButton;
    private ListView searchResultsList;
    private TextView errorMessageTextView;
    private DatabaseReference listingNode;

    private List<String> searchResultsData;
    private ArrayAdapter<String> adapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeUIElements();
        setupSearchButton();
        initializeFirebase();

        searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item
                String selectedItem = searchResultsData.get(position);

                // Start the SendRequestActivity or navigate to send_request_action.xml layout
                Intent intent = new Intent(SearchActivity.this, Transaction.class);
                // Pass any necessary data to the SendRequestActivity using intent extras
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
        });
    }

    private void initializeUIElements() {
        nameEditText = findViewById(R.id.nameEditText);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        exchangePreferenceEditText = findViewById(R.id.exchangePreferenceEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsList = findViewById(R.id.searchResultsListView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);

        searchResultsData = new ArrayList<>();
        adapter = createListAdapter();
        searchResultsList.setAdapter(adapter);
    }

    private ArrayAdapter<String> createListAdapter() {
        return new ArrayAdapter<String>(this, R.layout.list_item, searchResultsData) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = createOrReuseView(convertView, parent);
                TextView nameTextView = view.findViewById(R.id.nameTextView);
                TextView detailsTextView = view.findViewById(R.id.detailsTextView);
                String item = searchResultsData.get(position);
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

    private void setupSearchButton() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String condition = conditionSpinner.getSelectedItem().toString();
                String exchangePreference = exchangePreferenceEditText.getText().toString();
                if (hasNoSearchCriteria(name, condition, exchangePreference)) {
                    displayNoCriteriaError();
                } else {
                    searchWithData(name, condition, exchangePreference);
                }
            }
        });
    }

    private boolean hasNoSearchCriteria(String name, String condition, String exchangePreference) {
        return name.isEmpty() && condition.equals("Any") && exchangePreference.isEmpty();
    }

    private void displayNoCriteriaError() {
        errorMessageTextView.setText("Please enter search criteria");
        errorMessageTextView.setVisibility(View.VISIBLE);
        clearSearchResults();
    }

    private void searchWithData(String name, String condition, String exchangePreference) {
        clearSearchResults();
        queryFirebaseForSearchResults(name, condition, exchangePreference);
    }

    private void clearSearchResults() {
        searchResultsData.clear();
        adapter.notifyDataSetChanged();
    }

    private void queryFirebaseForSearchResults(String name, String condition, String exchangePreference) {
        listingNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processSearchResults(dataSnapshot, name, condition, exchangePreference);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    //added hidden and exchanged option
    private void processSearchResults(DataSnapshot dataSnapshot, String name, String condition, String exchangePreference) {
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            boolean isMarkedAsExchanged = Boolean.TRUE.equals(itemSnapshot.child("Marked As Exchanged").getValue(Boolean.class));
            boolean isHidden = Boolean.TRUE.equals(itemSnapshot.child("Hidden").getValue(Boolean.class));

            // Skip items that are marked as exchanged or hidden
            if (!isMarkedAsExchanged && !isHidden) {
                String itemName = itemSnapshot.child("Product Name").getValue(String.class);
                String itemCondition = itemSnapshot.child("Condition").getValue(String.class);
                String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                String itemExchangePref = itemSnapshot.child("Exchange Preference").getValue(String.class);
                String seller = itemSnapshot.child("Seller").getValue(String.class);
                String address = itemSnapshot.child("Address").getValue(String.class);

                if (matchesSearchCriteria(itemName, itemCondition, itemExchangePref, name, condition, exchangePreference)) {
                    addMatchingItemToResults(itemName, itemCondition, itemDescription, itemExchangePref, seller, address);
                }
            }
        }

        displaySearchResultsOrNoMatchError();
    }

    private boolean matchesSearchCriteria(String name, String condition, String exchangePref,
                                          String nameCriteria, String conditionCriteria, String exchangePrefCriteria) {
        boolean nameMatch = nameCriteria.trim().isEmpty() || name.toLowerCase().contains(nameCriteria.toLowerCase().trim());
        boolean conditionMatch = conditionCriteria.equals("Any") || condition.equals(conditionCriteria);
        boolean exchangePrefMatch = exchangePrefCriteria.trim().isEmpty() || exchangePref.toLowerCase().contains(exchangePrefCriteria.toLowerCase().trim());
        return nameMatch && conditionMatch && exchangePrefMatch;
    }

    private void addMatchingItemToResults(String itemName, String itemCondition, String itemDescription, String itemExchangePref, String seller, String address) {
        String item = itemName + "<br>" + "Condition: " + itemCondition + "\n" +
                "Description: " + itemDescription + "\n" + "Exchange Preference: " + itemExchangePref + "\n" + "Address: "
                + address + "\n" + "Seller: " + seller;
        searchResultsData.add(item);
    }

    private void displaySearchResultsOrNoMatchError() {
        if (searchResultsData.isEmpty()) {
            errorMessageTextView.setText("No matching items found");
            errorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            errorMessageTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        // Handle database error
    }

    private void initializeFirebase() {
        listingNode = FirebaseDatabase.getInstance().getReference("Listings"); // Replace with your
    }

}