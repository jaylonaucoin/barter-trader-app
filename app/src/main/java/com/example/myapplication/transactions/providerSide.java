package com.example.myapplication.transactions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class providerSide extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference receiver, provider;
    Button accept, decline;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_request_action);

        accept = (Button) findViewById(R.id.AcceptButton);
        decline = (Button) findViewById(R.id.declineButton);
        connectToDBase();

        Toast requestAccepted = Toast.makeText(getApplicationContext(),"Request Accepted" ,Toast.LENGTH_SHORT);
        Toast requestCancelled = Toast.makeText(getApplicationContext(),"Request Declined",Toast.LENGTH_SHORT);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAccepted.show();
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);


                // considering it is the reference to the provider
                provider = firebaseDB.getReference("providerUserName");
                requestAccepted(provider, receiver);
            }
        });


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCancelled.show();
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
            }
        });
    }


    private void connectToDBase(){
        firebaseDB = FirebaseDatabase.getInstance("https://transactionactivity-40673-default-rtdb.firebaseio.com/");

    }
    private void requestAccepted(DatabaseReference provider, DatabaseReference receiver){

        String providerName = provider.child("Name").toString();

        //request accepted on the user side
        DatabaseReference ref = receiver.child("Requests").child(provider.child("Name").toString()).child("Status");
        ref.setValue("Accepted");

        Map<String, String> action = new HashMap<>();

        // marking item status as sold
        action.put("Status", "Sold");
        provider.child("listings").child(receiver.child("Requests").child(receiver.child(providerName).toString()).toString()).setValue(action);
    }

    private void requestDeclined(DatabaseReference provider, DatabaseReference receiver){

        String providerName = provider.child("Name").toString();
        String receiverName = receiver.child("Name").toString();

        //request accepted on the user side
        DatabaseReference ref = receiver.child("Requests").child(provider.child(providerName).toString()).child("Status");
        ref.setValue("Rejected");

        // removing request from the provider side
        provider.child("RequestReceived").removeValue();
    }

}
