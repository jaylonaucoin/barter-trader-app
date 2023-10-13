package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        connectToFirebase();
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText password = findViewById(R.id.password);
                String passwordEntered = password.getText().toString();
                EditText confirmPassword = findViewById(R.id.confirmPassword);
                String confirmPasswordEntered = confirmPassword.getText().toString();
                EditText email = findViewById(R.id.email);
                String emailEntered = email.getText().toString();
                EditText fname = findViewById(R.id.firstName);
                String fnameEntered = fname.getText().toString();
                EditText lname = findViewById(R.id.lastName);
                String lnameEntered = lname.getText().toString();
                Spinner role = findViewById(R.id.role);
                String roleEntered = role.getSelectedItem().toString();
                String errorMessage = "";

                boolean allDataEntered = !passwordEntered.isEmpty() && !confirmPasswordEntered.isEmpty()
                        && !emailEntered.isEmpty() && !fnameEntered.isEmpty() && !lnameEntered.isEmpty()
                        && !roleEntered.isEmpty();

                if(allDataEntered) {
                    if(passwordEntered.equals(confirmPasswordEntered)){
                        auth.createUserWithEmailAndPassword(emailEntered, passwordEntered);
                        if(auth.getCurrentUser() != null){
                            DatabaseReference user = firebaseDB.getReference("User/" + auth.getCurrentUser().getUid());
                            user.child("firstName").setValue(fnameEntered);
                            user.child("lastName").setValue(lnameEntered);
                            user.child("role").setValue(roleEntered);
                            Intent intent = new Intent(RegisterActivity.this, RegisterSuccessActivity.class);
                            intent.putExtra("name", fnameEntered);
                            startActivity(intent);
                        }
                        else {
                            errorMessage = "Unknown error occurred. Please try again.";
                        }
                    }
                    else {
                        errorMessage = "Passwords do not match!";
                    }
                }
                else {
                    errorMessage = "Please fill out all fields!";
                }
                if(!errorMessage.equals("")){
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connectToFirebase(){
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("User");
        auth = FirebaseAuth.getInstance();
    }
}
