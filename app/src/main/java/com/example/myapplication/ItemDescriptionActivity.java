package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class ItemDescriptionActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_goods);


        Button submitButton = (Button) findViewById(R.id.submit_button);

        Toast successToast = Toast.makeText(getApplicationContext(),"All fields are filled" ,Toast.LENGTH_SHORT);
        Toast failToast = Toast.makeText(getApplicationContext(),"All fields must be filled",Toast.LENGTH_SHORT);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText prodName = (EditText) findViewById(R.id.name);
                EditText address = (EditText) findViewById(R.id.address);
                EditText condition = (EditText) findViewById(R.id.condition);
                EditText description = (EditText) findViewById(R.id.description);
                EditText preference =  (EditText) findViewById(R.id.preference);

                // Copying their string Value
                String prodValue = prodName.getText().toString().trim();
                String addressValue = address.getText().toString().trim();
                String conditionValue = condition.getText().toString().trim();
                String descriptionValue = description.getText().toString().trim();
                String preferenceValue = preference.getText().toString().trim();

                if(!prodValue.isEmpty() && !addressValue.isEmpty() && !conditionValue.isEmpty() && !descriptionValue.isEmpty() && !preferenceValue.isEmpty()){
                    successToast.show();
                    connectToDBase(prodValue);
                    writeToFireDB(addressValue, conditionValue, descriptionValue, preferenceValue);
                }else{
                    failToast.show();
                }
            }
        });

    }

    private void connectToDBase(String productName){
        firebaseDB = FirebaseDatabase.getInstance("https://my-application-89cfb-default-rtdb.firebaseio.com/");
        firebaseDBRef = firebaseDB.getReference(productName);
    }
    private void writeToFireDB(String address, String condition, String description, String preference){
        Map<String, String> details = new HashMap<>();
        details.put("Address", address);
        details.put("Condition", condition);
        details.put("Description", description);
        details.put("Exchange preference", preference);
        firebaseDBRef.setValue(details);
    }

}
