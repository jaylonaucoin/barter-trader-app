package com.example.myapplication;

<<<<<<< Updated upstream
public class ValuationService {
=======
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ValuationService extends AppCompatActivity {
    private TextView tvTotalValue;
    private EditText etItemName, etItemValue;
    private Button Sell, Buy;
    private ListView listView;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valuation_service);

        tvTotalValue = findViewById(R.id.tvTotalValue);
        etItemName = findViewById(R.id.etItemName);
        etItemValue = findViewById(R.id.etItemValue);
        Sell = findViewById(R.id.Sell);
        Buy = findViewById(R.id.Buy);
        listView = findViewById(R.id.listView);

        //build a new list to save the stuff
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.valuation_service, items);
        listView.setAdapter(adapter);



    }

>>>>>>> Stashed changes
}
