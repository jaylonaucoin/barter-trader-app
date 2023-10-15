package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< Updated upstream
import androidx.core.app.NotificationCompat;

=======
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
>>>>>>> Stashed changes
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
<<<<<<< Updated upstream
import android.os.Build;
import android.os.Bundle;
=======
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
>>>>>>> Stashed changes
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
<<<<<<< Updated upstream
=======
import android.widget.RemoteViews;
>>>>>>> Stashed changes
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


    TextView preferenceText;





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

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
        typeSpinner = findViewById(R.id.type);
        colorSpinner = findViewById(R.id.color);
        customerPreference = findViewById(R.id.add);
        submitButton = findViewById(R.id.submit);
        cancelButton = findViewById(R.id.cancelChange);
        saveButton = findViewById(R.id.save);
<<<<<<< Updated upstream


        preferenceText = findViewById(R.id.preference_text);


=======
        preferenceText = findViewById(R.id.preference_text);

>>>>>>> Stashed changes
        setupSpinners();
        connectToFirebase();

        submitButton.setOnClickListener(v -> onSubmit());
        cancelButton.setOnClickListener(v -> customerPreference.setText(""));
        saveButton.setOnClickListener(v -> onSave());
<<<<<<< Updated upstream


    }

    private void setupSpinners() {

    }

    void setupSpinners() {

=======
    }

    void setupSpinners() {
>>>>>>> Stashed changes
        // set up type spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_options, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // set up color spinner
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.color_options, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
<<<<<<< Updated upstream

    }
    //set up firebase
    public void savePreferencesToFirebase(String type, String color, String customText) {
=======
    }

    //set up firebase
    public void savePreferencesToFirebase(String type, String color, String customText) {


>>>>>>> Stashed changes
        Map<String, String> userPreferences = new HashMap<>();
        userPreferences.put("Type", type);
        userPreferences.put("Color", color);
        userPreferences.put("CustomText", customText);

        firebaseDBRef.child("Preference").child("SavedPreference").setValue(userPreferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Your information is upload", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "There's some issue cause the failed", Toast.LENGTH_SHORT).show());
<<<<<<< Updated upstream
    }

    public void onSubmit() {
        String type = typeSpinner.getSelectedItem().toString();
        String color = colorSpinner.getSelectedItem().toString();
        String customText = customerPreference.getText().toString();
        savePreferencesToFirebase(type, color, customText);

        // store the preferences
        Toast.makeText(this, "Preferences: " + type + ", " + color + ", " + customText, Toast.LENGTH_SHORT).show();
    }


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
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Your information is upload", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "There's some issue cause the failed", Toast.LENGTH_SHORT).show());
    }
=======
    }

>>>>>>> Stashed changes
    //add a method to check the input length not too long
    public static boolean checkStringLength(String input, int character) {
        return input.length() > character;
    }

    public void onSubmit() {
        String type = typeSpinner.getSelectedItem().toString();
        String color = colorSpinner.getSelectedItem().toString();
        String customText = customerPreference.getText().toString();
<<<<<<< Updated upstream
        if (checkStringLength(customText, 50)) {
            Toast.makeText(this, "No more than 50 characters", Toast.LENGTH_SHORT).show();
            return; // stop
        }
        savePreferencesToFirebase(type, color, customText);

        // store the preferences
        Toast.makeText(this, "Preferences: " + type + ", " + color + ", " + customText, Toast.LENGTH_SHORT).show();
    }


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
    //add a method to check the input length not too long
    public static boolean checkStringLength(String input, int character) {
        return input.length() > character;
    }

    public void onSubmit() {
        String type = typeSpinner.getSelectedItem().toString();
        String color = colorSpinner.getSelectedItem().toString();
        String customText = customerPreference.getText().toString();
        String displayText = "Preferences: \n" +"type: "+ type+"\n"+"color: "+ color + "\n" +"other: "+ customText;
=======
        String displayText = "Preferences: \n" + "type: " + type + "\n" + "color: " + color + "\n" + "other: " + customText;
>>>>>>> Stashed changes
        preferenceText.setText(displayText);
        if (checkStringLength(customText, 50)) {
            Toast.makeText(this, "No more than 50 characters", Toast.LENGTH_SHORT).show();
            return; // stop
        }
        savePreferencesToFirebase(type, color, customText);

        // store the preferences
        Toast.makeText(this, "Preferences: " + type + ", " + color + ", " + customText, Toast.LENGTH_SHORT).show();
<<<<<<< Updated upstream
    }

=======

    }
>>>>>>> Stashed changes

    public void onSave() {
        // saved message
        Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
    }

<<<<<<< Updated upstream
    public void connectToFirebase(){
        firebaseDB=FirebaseDatabase.getInstance("https://my-application-d814a-default-rtdb.firebaseio.com/");
        firebaseDBRef=firebaseDB.getReference("Profile");
    }
=======
    public void connectToFirebase() {
        firebaseDB = FirebaseDatabase.getInstance("https://my-application-d814a-default-rtdb.firebaseio.com/");
        firebaseDBRef = firebaseDB.getReference("Profile");
    }



>>>>>>> Stashed changes
    //end

}

