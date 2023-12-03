package com.example.myapplication.receiver_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class SearchFragment extends Fragment {
    private EditText nameEditText;
    private Spinner categorySpinner;
    private Spinner conditionSpinner;
    private EditText exchangePreferenceEditText;
    private Button searchButton;
    private TextView errorMessageTextView;
    private DatabaseReference listingNode;

    private List<ListItem> searchResultsData = new ArrayList<>();
    private ArrayAdapter<ListItem> adapter;
    private final HashMap<String, String> uidMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.receiver_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initializeUIElements(view);
        setupSearchButton();
        initializeFirebase();
    }

    private void initializeUIElements(View view) {
        nameEditText = view.findViewById(R.id.nameEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        conditionSpinner = view.findViewById(R.id.conditionSpinner);
        exchangePreferenceEditText = view.findViewById(R.id.exchangePreferenceEditText);
        searchButton = view.findViewById(R.id.searchButton);
        ListView searchResultsList = view.findViewById(R.id.searchResultsListView);
        errorMessageTextView = view.findViewById(R.id.errorMessageTextView);

        searchResultsData = new ArrayList<>();
        adapter = createListAdapter();
        searchResultsList.setAdapter(adapter);

        searchResultsList.setOnItemClickListener((parent, view1, position, id) -> {
            ListItem selectedItem = (ListItem) parent.getItemAtPosition(position);
            String sellerUid = selectedItem.getUserId();

            Intent intent = new Intent(getActivity(), UserProfile.class);
            intent.putExtra("uid", sellerUid);
            startActivity(intent);
        });
    }

    private ArrayAdapter<ListItem> createListAdapter() {
        return new ArrayAdapter<ListItem>(requireActivity(), R.layout.list_item, searchResultsData) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = createOrReuseView(convertView, parent);
                TextView nameTextView = view.findViewById(R.id.nameTextView);
                TextView detailsTextView = view.findViewById(R.id.detailsTextView);
                ListItem item = searchResultsData.get(position);
                displayItemDetails(nameTextView, detailsTextView, item.getDisplayText());
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
        searchButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();
            String condition = conditionSpinner.getSelectedItem().toString();
            String exchangePreference = exchangePreferenceEditText.getText().toString();
            if (hasNoSearchCriteria(name, category, condition, exchangePreference)) {
                displayNoCriteriaError();
            } else {
                searchWithData(name, category, condition, exchangePreference);
            }
        });
    }

    private boolean hasNoSearchCriteria(String name, String category, String condition, String exchangePreference) {
        return name.isEmpty() && category.equals("Any") && condition.equals("Any") && exchangePreference.isEmpty();
    }

    private void displayNoCriteriaError() {
        errorMessageTextView.setText("Please enter search criteria");
        errorMessageTextView.setVisibility(View.VISIBLE);
        clearSearchResults();
    }

    private void searchWithData(String name, String category, String condition, String exchangePreference) {
        clearSearchResults();
        queryFirebaseForSearchResults(name, category, condition, exchangePreference);
    }

    private void clearSearchResults() {
        searchResultsData.clear();
        adapter.notifyDataSetChanged();
    }

    private void queryFirebaseForSearchResults(String name, String category, String condition, String exchangePreference) {
        listingNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processSearchResults(dataSnapshot, name, category, condition, exchangePreference);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    private void processSearchResults(DataSnapshot dataSnapshot, String name, String category, String condition, String exchangePreference) {
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            String itemName = itemSnapshot.child("Product Name").getValue(String.class);
            String itemCategory = itemSnapshot.child("Category").getValue(String.class);
            String itemCondition = itemSnapshot.child("Condition").getValue(String.class);
            String itemDescription = itemSnapshot.child("Description").getValue(String.class);
            String itemExchangePref = itemSnapshot.child("Exchange Preference").getValue(String.class);
            String seller = itemSnapshot.child("Seller").getValue(String.class);
            String address = itemSnapshot.child("Address").getValue(String.class);
            String sellerUid = itemSnapshot.child("User ID").getValue(String.class);

            if (matchesSearchCriteria(itemName, itemCategory, itemCondition, itemExchangePref, name, category, condition, exchangePreference)) {
                addMatchingItemToResults(itemName, itemCategory, itemCondition, itemDescription, itemExchangePref, seller, address, sellerUid);
                uidMap.put(seller, sellerUid);
            }
        }
        displaySearchResultsOrNoMatchError();
    }

    private boolean matchesSearchCriteria(String itemName, String itemCategory, String itemCondition, String itemExchangePref, String name, String category, String condition, String exchangePreference) {
        boolean nameMatch = (itemName != null) && (name.isEmpty() || itemName.toLowerCase().contains(name.toLowerCase()));
        boolean categoryMatch = (itemCategory != null) && (category.equals("Any") || itemCategory.equals(category));
        boolean conditionMatch = (itemCondition != null) && (condition.equals("Any") || itemCondition.equals(condition));
        boolean exchangePrefMatch = (itemExchangePref != null) && (exchangePreference.isEmpty() || itemExchangePref.toLowerCase().contains(exchangePreference.toLowerCase()));

        return nameMatch && categoryMatch && conditionMatch && exchangePrefMatch;
    }


    private void addMatchingItemToResults(String itemName, String itemCategory, String itemCondition, String itemDescription, String itemExchangePref, String seller, String address, String sellerUid) {
        String displayText = itemName + "<br>" + "Category: " + itemCategory + "\n" +
                "Condition: " + itemCondition + "\n" +
                "Description: " + itemDescription + "\n" + "Exchange Preference: " + itemExchangePref + "\n" + "Address: "
                + address + "\n" + "Seller: " + seller;
        ListItem item = new ListItem(displayText, sellerUid);
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
        errorMessageTextView.setText("Database error occurred: " + databaseError.getMessage());
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void initializeFirebase() {
        listingNode = FirebaseDatabase.getInstance().getReference("Listings");
    }

    public static class ListItem {
        private final String displayText;
        private final String userId;

        public ListItem(String displayText, String userId) {
            this.displayText = displayText;
            this.userId = userId;
        }

        public String getDisplayText() {
            return displayText;
        }

        public String getUserId() {
            return userId;
        }
    }
}
