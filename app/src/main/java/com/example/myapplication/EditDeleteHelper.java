package com.example.myapplication;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditDeleteHelper {

    private final FirebaseDatabase firebaseDB;
    private final DatabaseReference firebaseDBRef;


    public EditDeleteHelper() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
    }

    public void markAsExchanged(String listingKey, Context context) {
        DatabaseReference listingRef = firebaseDBRef.child(listingKey);
        listingRef.child("Marked As Exchanged").setValue(true);
        Toast.makeText(context, "Listing exchanged", Toast.LENGTH_SHORT).show();
    }

    public void hideListing(String listingKey, Context context) {
        DatabaseReference listingRef = firebaseDBRef.child(listingKey);
        listingRef.child("Hidden").setValue(true);
        Toast.makeText(context, "Listing Hidden", Toast.LENGTH_SHORT).show();
    }

    public void unHideListing(String listingKey, Context context) {
        DatabaseReference listingRef = firebaseDBRef.child(listingKey);
        listingRef.child("Hidden").setValue(false);
        Toast.makeText(context, "Listing Unhidden", Toast.LENGTH_SHORT).show();
    }

    public void markAsAvailable(String listingKey, Context context) {
        DatabaseReference listingRef = firebaseDBRef.child(listingKey);
        listingRef.child("Marked As Exchanged").setValue(false);
        Toast.makeText(context, "Listing Available for Exchange", Toast.LENGTH_SHORT).show();
    }


}
