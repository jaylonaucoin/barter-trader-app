package com.example.myapplication.transactions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class transactions extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    Button request, cancel;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_transaction_request);

        request = (Button) findViewById(R.id.requestButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setVisibility(View.GONE);
        Toast requestSent = Toast.makeText(getApplicationContext(),"Exchange request has been sent" ,Toast.LENGTH_SHORT);
        Toast requestCancelled = Toast.makeText(getApplicationContext(),"Exchange request has been cancelled",Toast.LENGTH_SHORT);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSent.show();
                cancel.setVisibility(View.VISIBLE);
                request.setVisibility(View.GONE);
                connectToDBase();
                writeToDB();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCancelled.show();
                cancel.setVisibility(View.GONE);
                request.setVisibility(View.VISIBLE);
            }
        });
    }
    private void connectToDBase(){
        firebaseDB = FirebaseDatabase.getInstance("https://my-application-89cfb-default-rtdb.firebaseio.com/");
        firebaseDBRef = firebaseDB.getReference("User1");
    }

    private void writeToDB(){
        // User information
        Map<String, String> user = new HashMap<>();
        user.put("Name", "Rahul Kumar");
        user.put("Password", "123456" );

        // item information
        Map<String, String> listings = new HashMap<>();
        listings.put("Address", "address");
        listings.put("Condition", "condition");
        listings.put("Description", "description");
        listings.put("Exchange preference", "preference");

        Map<String, Map> data = new HashMap<>();
        data.put("User", user);
        data.put("listings", listings);


        firebaseDBRef.setValue(data);
    }
}
