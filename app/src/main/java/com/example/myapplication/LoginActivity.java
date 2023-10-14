package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        // retrieving the password recovery button
        Button passwordRecoverButton = findViewById(R.id.passwordRecoverButton);
        passwordRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when password retrieved is clicked
            public void onClick(View v) {
                // bringing you to password recover page
                Intent passwordRecoveryIntent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
                startActivity(passwordRecoveryIntent);
            }
        });

        // retrieving the register button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when register is clicked
            public void onClick(View v) {
                // bringing you to register page
                Intent registerButtonIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerButtonIntent);
            }
        });

        // retrieving the login button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            // on click for when login is clicked
            public void onClick(View v) {
                // grabbing the users inputted information (email and password)
                EditText emailEditText = findViewById(R.id.email);
                EditText passwordEditText = findViewById(R.id.password);
                String userEmail = emailEditText.getText().toString();
                String userPassword = passwordEditText.getText().toString();

                // checking if either the password or email is empty
                if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
                    // message to inform user to fill out both
                    Toast.makeText(LoginActivity.this, "Both email and password are required.", Toast.LENGTH_SHORT).show();
                }
                // if both filled out then conduct firebase auth pre-made sign in method
                else {
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // if users credentials match from the database then display success and bring them to login success page
                                    if (task.isSuccessful()) {
                                        // login was completed
                                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        Intent loginSuccessIntent = new Intent(LoginActivity.this, LoginSuccessActivity.class);
                                        startActivity(loginSuccessIntent);
                                    }
                                    // if the credentials do not match any from the firebase then inform user and keep them on login page
                                    else {
                                        Toast.makeText(LoginActivity.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void connectToFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("test");
    }
}