package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;

    Spinner typeSpinner, colorSpinner;
    EditText customerPreference;
    Button submitButton, cancelButton, saveButton;


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

        setupSpinners();
        connectToFirebase();

        submitButton.setOnClickListener(v -> onSubmit());
        cancelButton.setOnClickListener(v -> customerPreference.setText(""));
        saveButton.setOnClickListener(v -> onSave());
    }

    private void setupSpinners() {
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
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Your information is upload", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "There's some issue cause the failed", Toast.LENGTH_SHORT).show());
    }

    public void onSubmit() {
        String type = typeSpinner.getSelectedItem().toString();
        String color = colorSpinner.getSelectedItem().toString();
        String customText = customerPreference.getText().toString();
        savePreferencesToFirebase(type, color, customText);

        // store the preferences
        Toast.makeText(this, "Preferences: " + type + ", " + color + ", " + customText, Toast.LENGTH_SHORT).show();
    }

    public void onSave() {
        // saved message
        Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
    }

    public void connectToFirebase(){
        firebaseDB=FirebaseDatabase.getInstance("https://my-application-d814a-default-rtdb.firebaseio.com/");
        firebaseDBRef=firebaseDB.getReference("Profile");
    }
    //end

}

