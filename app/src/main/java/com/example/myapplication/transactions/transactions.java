package com.example.myapplication.transactions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class transactions extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference receiver, provider;
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

                // considering it is the reference to the provider
                provider = firebaseDB.getReference("providerUserName");
                retrieveData(provider);
                addRequest(provider, receiver);
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
        firebaseDB = FirebaseDatabase.getInstance("https://transactionactivity-40673-default-rtdb.firebaseio.com/");

    }

    // this method add request on the receiver side
    private void addRequest(String providerName, String listingName){
        //creating a hashmap, and storing providerName, and listing name to receiver requests
        Map<String, String> req = new HashMap<>();
        req.put(providerName,listingName);

        Map<String, String> status = new HashMap<>();
        req.put("Status","Pending");
        receiver = firebaseDB.getReference("receiverUserName");
        receiver.child("Requests").setValue(req);
        receiver.child("Requests").child(providerName).setValue(status);
    }

    // this method retrieves the name of provider and the product name
    private void retrieveData(DatabaseReference provider){
        provider.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    final String providerName = snapshot.child("UserName").getValue(String.class);
                    final String listingName = snapshot.child("Listing").child("Name").toString();
                    addRequest(providerName,listingName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // this method will add the receiver name and listing Name to provider
    private void addRequest(DatabaseReference provider, DatabaseReference receiver){
        String receiverName = receiver.child("UserName").toString();
        String productName = receiver.child("Requests").child(provider.child("Name").toString()).toString();
        Map<String, String > itemReq = new HashMap<>();
        itemReq.put(receiverName, productName);
        provider.child("RequestReceived").setValue(itemReq);
    }
}
