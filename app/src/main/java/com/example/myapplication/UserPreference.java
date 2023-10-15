package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import java.util.Map;

public class UserPreference extends AppCompatActivity {



    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private String textContent = "";

    Spinner typeSpinner, colorSpinner;
    EditText customerPreference;
    Button submitButton, cancelButton, saveButton;
    TextView preferenceText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //work here


        typeSpinner = findViewById(R.id.type);
        colorSpinner = findViewById(R.id.color);
        customerPreference = findViewById(R.id.add);
        submitButton = findViewById(R.id.submit);
        cancelButton = findViewById(R.id.cancelChange);
        saveButton = findViewById(R.id.save);
        preferenceText = findViewById(R.id.preference_text);

        setupSpinners();
        connectToFirebase();

        submitButton.setOnClickListener(v -> onSubmit());
        cancelButton.setOnClickListener(v -> customerPreference.setText(""));
        saveButton.setOnClickListener(v -> onSave());
    }

    void setupSpinners() {
        // set up type spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_options, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // set up color spinner
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.color_options, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
    }

    //set up firebase
    public void savePreferencesToFirebase(String type, String color, String customText) {


        Map<String, String> userPreferences = new HashMap<>();
        userPreferences.put("Type", type);
        userPreferences.put("Color", color);
        userPreferences.put("CustomText", customText);

        firebaseDBRef.child("Preference").child("SavedPreference").setValue(userPreferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(UserPreference.this, "Your information is upload", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UserPreference.this, "There's some issue cause the failed", Toast.LENGTH_SHORT).show());
    }

    //add a method to check the input length not too long
    public static boolean checkStringLength(String input, int character) {
        return input.length() > character;
    }

    public void onSubmit() {
        String type = typeSpinner.getSelectedItem().toString();
        String color = colorSpinner.getSelectedItem().toString();
        String customText = customerPreference.getText().toString();
        String displayText = "Preferences: \n" + "type: " + type + "\n" + "color: " + color + "\n" + "other: " + customText;
        preferenceText.setText(displayText);
        if (checkStringLength(customText, 50)) {
            Toast.makeText(this, "No more than 50 characters", Toast.LENGTH_SHORT).show();
            return; // stop
        }
        savePreferencesToFirebase(type, color, customText);

        // store the preferences
        Toast.makeText(this, "Preferences: " + type + ", " + color + ", " + customText, Toast.LENGTH_SHORT).show();

    }

    public void onSave() {
        // saved message
        Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
    }

    public void connectToFirebase() {
        firebaseDB = FirebaseDatabase.getInstance("https://my-application-d814a-default-rtdb.firebaseio.com/");
        firebaseDBRef = firebaseDB.getReference("Profile");
    }

    //end

}

