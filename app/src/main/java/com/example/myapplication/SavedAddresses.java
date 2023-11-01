package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SavedAddresses extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userId;
    private RecyclerView recyclerView;
    private final List<Map<String, Object>> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_addresses); // Replace with the name of your layout XML

        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to view saved addresses.", Toast.LENGTH_SHORT).show();
            finish();  // Exit this activity
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // Assuming you're using Firebase Auth

        recyclerView = findViewById(R.id.address_recycler);

        fetchAddresses();
    }

    private void fetchAddresses() {
        DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");
        userAddressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addressList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> address = (Map<String, Object>) snapshot.getValue();
                    addressList.add(address);
                }
                setupRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void setupRecyclerView() {
        AddressAdapter addressAdapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

        private final List<Map<String, Object>> addresses;

        public AddressAdapter(List<Map<String, Object>> addresses) {
            this.addresses = addresses;
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // You need to create an XML layout for each address item in the RecyclerView
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            Map<String, Object> address = addresses.get(position);

            // Assuming your address is stored in the 'address' key in your Firebase database
            String addressDetails = Objects.requireNonNull(address.get("address")).toString();
            holder.addressText.setText(addressDetails);

            if(position == 0) {
                holder.itemView.setBackgroundResource(R.color.purple_tint);  // Assuming you have a color resource for the purple tint.
            } else {
                holder.itemView.setBackgroundResource(R.color.white); // Default white background.
            }

            holder.itemView.setOnClickListener(v -> {
                // Rearrange the addresses such that clicked one becomes the top address
                Map<String, Object> clickedAddress = addresses.remove(position);
                addresses.add(0, clickedAddress);
                notifyDataSetChanged();

                updateAddressesInDatabase();
            });

            holder.removeIcon.setOnClickListener(v -> {
                addresses.remove(position);
                notifyDataSetChanged();

                updateAddressesInDatabase();
            });
        }


        @Override
        public int getItemCount() {
            return addresses.size();
        }

        private void updateAddressesInDatabase() {
            DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");
            userAddressRef.setValue(addresses)
                    .addOnSuccessListener(aVoid -> Toast.makeText(SavedAddresses.this, "Addresses updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(SavedAddresses.this, "Error updating addresses: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        class AddressViewHolder extends RecyclerView.ViewHolder {

            TextView addressText;
            ImageView removeIcon;

            public AddressViewHolder(@NonNull View itemView) {
                super(itemView);
                addressText = itemView.findViewById(R.id.address_text);
                removeIcon = itemView.findViewById(R.id.remove_address_icon);
            }
        }

    }
}
