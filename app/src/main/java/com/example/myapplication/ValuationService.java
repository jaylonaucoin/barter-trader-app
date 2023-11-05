package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ValuationService extends AppCompatActivity {
    private TextView tvTotalValue;
    private EditText etItemName, etItemValue;
    private Button Sell, Buy;
    private ListView listView;
    private ArrayList<String> items;
    private ArrayList<String> keys; // key use to save the public
    private ArrayAdapter<String> adapter;
    private ExchangeCalculate calculate;
    private DatabaseReference userDatabaseRef, publicItemsRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valuation_service);
        //get the user id in success activity and bulid database
        userId = getIntent().getStringExtra("USER_ID");
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("User/" + userId);
        publicItemsRef = FirebaseDatabase.getInstance().getReference("publicItems");

        tvTotalValue = findViewById(R.id.tvTotalValue);
        etItemName = findViewById(R.id.etItemName);
        etItemValue = findViewById(R.id.etItemValue);
        Sell = findViewById(R.id.Sell);
        Buy = findViewById(R.id.Buy);
        listView = findViewById(R.id.listView);

        //build a new list to save the stuff
        items = new ArrayList<>();
        keys = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_extend, items);
        listView.setAdapter(adapter);
        //listen to database change and update
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double initialTotalValue = 0;
                if (dataSnapshot.hasChild("totalValue")) {
                    initialTotalValue = dataSnapshot.child("totalValue").getValue(Double.class);
                }
                calculate = new ExchangeCalculate(items, initialTotalValue);
                tvTotalValue.setText("Your total value: " + calculate.getTotalValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error="error";
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
            }
        });
        //listen to public database change and update
        publicItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                keys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    items.add(snapshot.getValue(String.class));
                    keys.add(snapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error="error";
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
            }
        });
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
            builder.setPositiveButton("是的", (dialog, which) -> {
                //if chose yes
                calculate.sellItem(itemName, itemValue);
                tvTotalValue.setText("Your total value: " + calculate.getTotalValue());
                adapter.notifyDataSetChanged();

                publicItemsRef.push().setValue(itemName + " - " + itemValue);
                userDatabaseRef.child("totalValue").setValue(calculate.getTotalValue());
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
                //check if user can not buy with enough value
                if (!calculate.AbleToBuy(position)) {
                    Toast.makeText(ValuationService.this, "You do not have enough value to buy " + itemName, Toast.LENGTH_SHORT).show();
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
                            userDatabaseRef.child("totalValue").setValue(calculate.getTotalValue());

                            //replace the item that already sell
                            publicItemsRef.child(keys.get(position)).removeValue();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}
