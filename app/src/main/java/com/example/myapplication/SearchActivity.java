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

import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    // Declare UI elements
    private EditText nameEditText;
    private Spinner categorySpinner;
    private Spinner conditionSpinner;
    private EditText exchangePreferenceEditText;
    private Button searchButton;
    private ListView searchResultsList;
    private TextView errorMessageTextView;
    private DatabaseReference listingNode;

    // Declare variables for search results
    private List<String> searchResultsData;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> uidMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI elements, search button setup, and Firebase
        initializeUIElements();
        setupSearchButton();
        initializeFirebase();
    }

    // Initialize UI elements
    private void initializeUIElements() {
        // Find UI elements by their IDs
        nameEditText = findViewById(R.id.nameEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        exchangePreferenceEditText = findViewById(R.id.exchangePreferenceEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsList = findViewById(R.id.searchResultsListView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);

        // Initialize list and adapter for search results
        searchResultsData = new ArrayList<>();
        adapter = createListAdapter();
        searchResultsList.setAdapter(adapter);

        searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click here
                String selectedItem = (String) searchResultsList.getItemAtPosition(position);

                String sellerUid;

                String[] parts = selectedItem.split("<br>");
                String[] parts2 = parts[1].split("\n");
                String[] sellerString = parts2[4].split(":");
                String sellerName = sellerString[1].trim();
                sellerUid = uidMap.get(sellerName);

                // Create an intent to start another activity
                Intent intent = new Intent(SearchActivity.this, UserProfile.class);

                // Add any necessary data to the intent using putExtra (if needed)
                intent.putExtra("uid", sellerUid);

                // Start the activity
                startActivity(intent);
            }
        });
    }

    // Create an adapter for the list view
    private ArrayAdapter<String> createListAdapter() {
        return new ArrayAdapter<String>(this, R.layout.list_item, searchResultsData) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                // Create or reuse the view for list items
                View view = createOrReuseView(convertView, parent);
                TextView nameTextView = view.findViewById(R.id.nameTextView);
                TextView detailsTextView = view.findViewById(R.id.detailsTextView);
                String item = searchResultsData.get(position);
                displayItemDetails(nameTextView, detailsTextView, item);
                return view;
            }
        };
    }

    // Create or reuse a view for list items
    private View createOrReuseView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the layout for list items if the view is null
            LayoutInflater inflater = getLayoutInflater();
            return inflater.inflate(R.layout.list_item, parent, false);
        }
        // Return the converted view if it's not null
        return convertView;
    }

    // Display details of an item in the list view
    private void displayItemDetails(TextView nameTextView, TextView detailsTextView, String item) {
        // Split the item details if applicable
        String[] parts = item.split("<br>");
        if (parts.length >= 1) {
            // Extract name and details from the item
            String name = parts[0];
            String details = (parts.length > 1) ? parts[1] : "";
            // Make the name text bold
            Spannable spannable = makeNameTextBold(name);
            nameTextView.setText(spannable); // Set bold name text to the TextView
            detailsTextView.setText(details); // Set details to the TextView
        } else {
            // If there are no parts, set the entire item as text in the TextView
            nameTextView.setText(item);
            detailsTextView.setText("");
        }
    }

    // Make the name text bold
    private Spannable makeNameTextBold(String name) {
        // Create a SpannableString to make the text bold
        Spannable spannable = new SpannableString(name);
        // Set the style to bold for the entire text
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable; // Return the bold SpannableString
    }

    // Set up click listener for the search button
    private void setupSearchButton() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get values from EditTexts and Spinners
                String name = nameEditText.getText().toString();
                String condition = conditionSpinner.getSelectedItem().toString();
                String category = categorySpinner.getSelectedItem().toString();
                String exchangePreference = exchangePreferenceEditText.getText().toString();
                // Check if there are no search criteria
                if (hasNoSearchCriteria(name, category, condition, exchangePreference)) {
                    // Display error message for no criteria
                    displayNoCriteriaError();
                } else {
                    // Perform search with provided data
                    searchWithData(name, category, condition, exchangePreference);
                }
            }
        });
    }

    // Check if there are no search criteria entered
    private boolean hasNoSearchCriteria(String name, String category, String condition, String exchangePreference) {
        return name.isEmpty() && category.equals("Any") && condition.equals("Any") && exchangePreference.isEmpty();
    }

    // Display error message for no search criteria entered
    private void displayNoCriteriaError() {
        errorMessageTextView.setText("Please enter search criteria");
        errorMessageTextView.setVisibility(View.VISIBLE); // Set visibility to make error message visible
        clearSearchResults(); // Clear search results from the list
    }

    // Perform search with provided data
    private void searchWithData(String name, String category, String condition, String exchangePreference) {
        clearSearchResults(); // Clear previous search results
        queryFirebaseForSearchResults(name, category, condition, exchangePreference); // Query Firebase for search results
    }

    // Clear search results from the list
    private void clearSearchResults() {
        searchResultsData.clear(); // Clear the list of search results
        adapter.notifyDataSetChanged(); // Notify adapter to update the list view
    }

    // Query Firebase for search results based on search criteria
    private void queryFirebaseForSearchResults(String name, String category, String condition, String exchangePreference) {
        listingNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processSearchResults(dataSnapshot, name, category, condition, exchangePreference); // Process received search results
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError); // Handle database error if any
            }
        });
    }

    // Process received search results from Firebase
    private void processSearchResults(DataSnapshot dataSnapshot, String name, String category, String condition, String exchangePreference) {
        // Iterate through each item in received search results
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            // Get item details from Firebase snapshot
            String itemName = itemSnapshot.child("Product Name").getValue(String.class);
            String itemCategory = itemSnapshot.child("Category").getValue(String.class);
            String itemCondition = itemSnapshot.child("Condition").getValue(String.class);
            String itemDescription = itemSnapshot.child("Description").getValue(String.class);
            String itemExchangePref = itemSnapshot.child("Exchange Preference").getValue(String.class);
            String seller = itemSnapshot.child("Seller").getValue(String.class);
            String address = itemSnapshot.child("Address").getValue(String.class);
            String sellerUid = itemSnapshot.child("User ID").getValue(String.class);


            // Check if the item matches the search criteria
            if (matchesSearchCriteria(itemName, itemCategory, itemCondition, itemExchangePref, name, category, condition, exchangePreference)) {
                // Add the matching item to the search results list
                addMatchingItemToResults(itemName, itemCategory, itemCondition, itemDescription, itemExchangePref, seller, address);
                uidMap.put(seller, sellerUid);
            }
        }
        // Display search results or no match error based on search outcome
        displaySearchResultsOrNoMatchError();
    }

    // Check if an item matches the search criteria
    private boolean matchesSearchCriteria(String name, String category, String condition, String exchangePref,
                                          String nameCriteria, String categoryCriteria, String conditionCriteria, String exchangePrefCriteria) {
        // Check for matches based on provided search criteria
        boolean nameMatch = nameCriteria.trim().isEmpty() || name.toLowerCase().contains(nameCriteria.toLowerCase().trim());
        boolean categoryMatch = categoryCriteria.equals("Any") || category.equals(categoryCriteria);
        boolean conditionMatch = conditionCriteria.equals("Any") || condition.equals(conditionCriteria);
        boolean exchangePrefMatch = exchangePrefCriteria.trim().isEmpty() || exchangePref.toLowerCase().contains(exchangePrefCriteria.toLowerCase().trim());
        return nameMatch && categoryMatch && conditionMatch && exchangePrefMatch;
    }

    // Add a matching item to the search results list
    private void addMatchingItemToResults(String itemName, String itemCategory, String itemCondition, String itemDescription, String itemExchangePref, String seller, String address) {
        // Format item details and add to the search results list
        String item = itemName + "<br>" + "Category: " + itemCategory + "\n" +
                "Condition: " + itemCondition + "\n" +
                "Description: " + itemDescription + "\n" + "Exchange Preference: " + itemExchangePref + "\n" + "Address: "
                + address + "\n" + "Seller: " + seller;
        searchResultsData.add(item); // Add item to the list of search results
    }

    // Display search results or no match error
    private void displaySearchResultsOrNoMatchError() {
        // Show error message if no search results found, otherwise hide the error message
        if (searchResultsData.isEmpty()) {
            errorMessageTextView.setText("No matching items found");
            errorMessageTextView.setVisibility(View.VISIBLE); // Set visibility to make error message visible
        } else {
            errorMessageTextView.setVisibility(View.GONE); // Set visibility to hide the error message
        }
        adapter.notifyDataSetChanged(); // Notify adapter to update the list view
    }

    // Handle database error
    private void handleDatabaseError(DatabaseError databaseError) {
        // Handle database error if necessary
    }

    // Initialize Firebase database reference
    private void initializeFirebase() {
        listingNode = FirebaseDatabase.getInstance().getReference("Listings"); // Replace with your own database reference
    }
}