package com.example.myapplication;

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
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        exchangePreferenceEditText = findViewById(R.id.exchangePreferenceEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsList = findViewById(R.id.searchResultsListView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);

        searchResultsData = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, searchResultsData) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    view = inflater.inflate(R.layout.list_item, parent, false);
                }

                TextView nameTextView = view.findViewById(R.id.nameTextView);
                TextView detailsTextView = view.findViewById(R.id.detailsTextView);

                String item = searchResultsData.get(position);

                // Separate the name from other details
                String[] parts = item.split("<br>");
                if (parts.length >= 1) {
                    String name = parts[0];
                    String details = "";

                    if (parts.length > 1) {
                        details = parts[1];
                    }

                    // Set the name as bold text
                    Spannable spannable = new SpannableString(name);
                    spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    nameTextView.setText(spannable);
                    detailsTextView.setText(details);
                } else {
                    nameTextView.setText(item);
                    detailsTextView.setText("");
                }

                return view;
            }
        };

        searchResultsList.setAdapter(adapter);

        listingNode = FirebaseDatabase.getInstance().getReference("Listings"); // Replace with your Firebase path

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve user input
                String name = nameEditText.getText().toString();
                String condition = conditionSpinner.getSelectedItem().toString();
                String exchangePreference = exchangePreferenceEditText.getText().toString();

                if (name.isEmpty() && condition.equals("Any") && exchangePreference.isEmpty()) {
                    // Display an error message if all criteria are missing
                    errorMessageTextView.setText("Please enter search criteria");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    searchResultsData.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    // Clear existing results
                    searchResultsData.clear();
                    adapter.notifyDataSetChanged();

                    // Query the data
                    listingNode.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                String itemName = itemSnapshot.child("Product Name").getValue(String.class);
                                String itemCondition = itemSnapshot.child("Condition").getValue(String.class);
                                String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                                String itemExchangePref = itemSnapshot.child("Exchange Preference").getValue(String.class);

                                // Perform client-side filtering
                                if (matchesCriteria(itemName, itemCondition, itemExchangePref, name, condition, exchangePreference)) {
                                    // Item matches the criteria, add it to the results
                                    String item = itemName + "<br>" + "Condition: " + itemCondition + "\n" + "Description: " + itemDescription + "\n" + "Exchange Preference: " + itemExchangePref;
                                    searchResultsData.add(item);
                                }
                            }

                            if (searchResultsData.isEmpty()) {
                                errorMessageTextView.setText("No matching items found");
                                errorMessageTextView.setVisibility(View.VISIBLE);
                            } else {
                                errorMessageTextView.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                }
            }
        });
    }

    private boolean matchesCriteria(
            String name, String condition, String exchangePref,
            String nameCriteria, String conditionCriteria, String exchangePrefCriteria
    ) {
        boolean nameMatch = nameCriteria.isEmpty() || name.contains(nameCriteria);
        boolean conditionMatch = conditionCriteria.equals("Any") || condition.equals(conditionCriteria);
        boolean exchangePrefMatch = exchangePrefCriteria.isEmpty() || exchangePref.contains(exchangePrefCriteria);
        return nameMatch && conditionMatch && exchangePrefMatch;
    }
}
