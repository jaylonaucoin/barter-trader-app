package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ValuationService extends AppCompatActivity {
    private TextView tvTotalValue;
    private EditText etItemName, etItemValue;
    private Button Sell, Buy;
    private ListView listView;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private ExchangeCalculate calculate;

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();


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
            adapter = new ArrayAdapter<>(this, R.layout.list_extend, items);
            listView.setAdapter(adapter);
            calculate = new ExchangeCalculate(items);
            //behavior on sell button
            Sell.setOnClickListener(v -> {
                String itemName = etItemName.getText().toString();
                double itemValue;

                // avoid nothing input
                try {
                    itemValue = Double.parseDouble(etItemValue.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter the price.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //have a dialog to make sure for user
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure to sell?");
                builder.setMessage("You want sell " + itemName + " and get " + itemValue + " dollars in your total value ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    //if chose yes
                    calculate.sellItem(itemName, itemValue);
                    tvTotalValue.setText("Your total value: " + calculate.getTotalValue());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ValuationService.this, "Great you sell " + itemName + " !", Toast.LENGTH_SHORT).show();

                    databaseRef.child("totalValue").setValue(calculate.getTotalValue());
                    databaseRef.child("items").setValue(items);
                });
                //if chose no
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            });
            //behavior on buy button
            Buy.setOnClickListener(v -> {
                int position = listView.getCheckedItemPosition();
                if (position != ListView.INVALID_POSITION) {
                    String selectedItem = items.get(position);
                    String[] parts = selectedItem.split(" - ");
                    String itemName = parts[0];
                    if (!calculate.AbleToBuy(position)) {
                        Toast.makeText(ValuationService.this, "You do not have enough value " + itemName, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //same as sell button
                    new AlertDialog.Builder(ValuationService.this)
                            .setTitle("Confirm to buy")
                            .setMessage("Are you sure you want to buy " + itemName + " ?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                calculate.buyItem(position);
                                tvTotalValue.setText("Your total value: " + calculate.getTotalValue());
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ValuationService.this, "Great! you got " + itemName + " !", Toast.LENGTH_SHORT).show();

                                databaseRef.child("totalValue").setValue(calculate.getTotalValue());
                                databaseRef.child("items").setValue(items);

                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }
}