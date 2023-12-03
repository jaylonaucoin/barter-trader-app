package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostGoods extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;
    private DatabaseReference publicItemsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_goods);
        connectToDBase();

        publicItemsRef = FirebaseDatabase.getInstance().getReference("publicItems");

        Button submitButton = findViewById(R.id.submit_button);

        Toast successToast = Toast.makeText(getApplicationContext(),"Item uploaded successfully" ,Toast.LENGTH_SHORT);
        Toast failToast = Toast.makeText(getApplicationContext(),"All fields must be filled",Toast.LENGTH_SHORT);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText prodName = findViewById(R.id.name);
                Spinner condition = findViewById(R.id.condition);
                EditText description = findViewById(R.id.description);
                EditText preference = findViewById(R.id.preference);
                EditText moneyEditText = findViewById(R.id.money);

                // Copying their string Value
                String prodValue = prodName.getText().toString().trim();
                String conditionValue = condition.getSelectedItem().toString().trim();
                String descriptionValue = description.getText().toString().trim();
                String preferenceValue = preference.getText().toString().trim();
                String moneyValue = moneyEditText.getText().toString().trim();

                if(!prodValue.isEmpty() && !conditionValue.isEmpty() && !descriptionValue.isEmpty() && !preferenceValue.isEmpty() && !moneyValue.isEmpty()){
                    successToast.show();
                    writeToFireDB(prodValue, conditionValue, descriptionValue, preferenceValue, moneyValue);
                    Intent searchIntent = new Intent(PostGoods.this, SearchActivity.class);
                    startActivity(searchIntent);
                }else{
                    failToast.show();
                }
            }
        });
    }

    private void connectToDBase(){
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("Listings");
        auth = FirebaseAuth.getInstance();
    }
    private void writeToFireDB(String name, String condition, String description, String preference, String money){
        String id = firebaseDBRef.push().getKey();
        String uid = auth.getCurrentUser().getUid();

        DatabaseReference userRef = firebaseDB.getReference("User").child(uid);
        DatabaseReference addressRef = userRef.child("addresses").child("0");
        firebaseDBRef = firebaseDB.getReference("Listings/" + id);
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);
                String address = snapshot.child("address").getValue(String.class);

                firebaseDBRef.child("Address").setValue(address);
                firebaseDBRef.child("Latitude").setValue(latitude);
                firebaseDBRef.child("Longitude").setValue(longitude);
                firebaseDBRef.child("Price").setValue(money);
                String itemInfo = condition + " - " + name + " - " + description + " - " + preference + " - " + money + " - " + uid;
                publicItemsRef.push().setValue(itemInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue(String.class);
                String lastName = snapshot.child("lastName").getValue(String.class);

                firebaseDBRef.child("Seller").setValue(firstName + " " + lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        updateUserTotalValue(money);


        firebaseDBRef.child("User ID").setValue(uid);
        firebaseDBRef.child("Product Name").setValue(name);
        firebaseDBRef.child("Description").setValue(description);
        firebaseDBRef.child("Condition").setValue(condition);
        firebaseDBRef.child("Exchange Preference").setValue(preference);


    }
    private void updateUserTotalValue(String money) {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference userRef = firebaseDB.getReference("User").child(uid);

        userRef.child("totalValue").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double currentTotalValue = 0;
                if (snapshot.exists()) {
                    currentTotalValue = snapshot.getValue(Double.class);
                }
                double updatedTotalValue = currentTotalValue + Double.parseDouble(money);

                // update the data in firebase
                userRef.child("totalValue").setValue(updatedTotalValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //do with error
            }
        });
    }
}