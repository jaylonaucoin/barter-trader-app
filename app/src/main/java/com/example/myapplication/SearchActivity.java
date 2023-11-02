package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText productTypeEditText;
    private EditText locationEditText;
    private Button searchButton;
    private ListView searchResultsList;
    private TextView errorMessageTextView;

    private List<String> searchResultsData; // Data source for the ListView
    private ArrayAdapter<String> adapter; // ArrayAdapter for the ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI elements
        productTypeEditText = findViewById(R.id.productTypeEditText);
        locationEditText = findViewById(R.id.locationEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsList = findViewById(R.id.searchResultsListView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);

        // Initialize the data source and ArrayAdapter for the ListView
        searchResultsData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResultsData);
        searchResultsList.setAdapter(adapter);

        // Set click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve user input
                String productType = productTypeEditText.getText().toString();
                String location = locationEditText.getText().toString();

                if (productType.isEmpty() || location.isEmpty()) {
                    // Display an error message if criteria are missing
                    errorMessageTextView.setText("Please enter search criteria");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    searchResultsData.clear(); // Clear existing search results
                    adapter.notifyDataSetChanged();
                } else {
                    // Implement your search logic here and update the search results
                    // For this example, we'll add dummy data to the ListView
                    searchResultsData.add("Search Result 1");
                    searchResultsData.add("Search Result 2");
                    searchResultsData.add("Search Result 3");
                    errorMessageTextView.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
