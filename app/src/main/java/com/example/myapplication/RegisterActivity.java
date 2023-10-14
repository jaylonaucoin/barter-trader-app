package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    // Firebase references
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect to Firebase DB
        connectToFirebase();

        // Find and configure the register button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input data
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
                final String[] errorMessage = {""}; // Initialize an error message

                // Check if all required fields are entered
                boolean allDataEntered = !passwordEntered.isEmpty() && !confirmPasswordEntered.isEmpty()
                        && !emailEntered.isEmpty() && !fnameEntered.isEmpty() && !lnameEntered.isEmpty()
                        && !roleEntered.isEmpty();

                if (allDataEntered) {
                    // Validate email
                    if(Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches()) {
                        // Check if passwords match
                        if(passwordEntered.equals(confirmPasswordEntered)) {
                            // Check password length according to Firebase rules (6 chars long)
                            if(passwordEntered.length() >= 6) {
                                // Create a user using Firebase authentication
                                auth.createUserWithEmailAndPassword(emailEntered, passwordEntered)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Registration is successful
                                                    FirebaseUser user = auth.getCurrentUser();
                                                    if (user != null) {
                                                        // Update user information in the Realtime Database
                                                        DatabaseReference userRef = firebaseDB.getReference("User/" + user.getUid());
                                                        userRef.child("firstName").setValue(fnameEntered);
                                                        userRef.child("lastName").setValue(lnameEntered);
                                                        userRef.child("role").setValue(roleEntered);

                                                        // Proceed to the success page
                                                        Intent intent = new Intent(RegisterActivity.this, RegisterSuccessActivity.class);
                                                        intent.putExtra("name", fnameEntered);
                                                        startActivity(intent);
                                                    }
                                                } else {
                                                    // Handle different error cases starting here
                                                    try {
                                                        throw Objects.requireNonNull(task.getException());
                                                    } catch (FirebaseAuthUserCollisionException e) {
                                                        errorMessage[0] = "Email is already in use!";
                                                    } catch (Exception e) {
                                                        errorMessage[0] = "Unknown error occurred. Please try again.";
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                errorMessage[0] = "Password must be at least 6 characters!";
                            }
                        } else {
                            errorMessage[0] = "Passwords do not match!";
                        }
                    } else {
                        errorMessage[0] = "Please enter a valid email address!";
                    }
                } else {
                    errorMessage[0] = "Please fill out all fields!";
                }

                // Display the error message if any
                if (!errorMessage[0].isEmpty()) {
                    Toast.makeText(getApplicationContext(), errorMessage[0], Toast.LENGTH_SHORT).show();
                }
            }
        });
        // grabbing the return to login page button
        Button loginReturnButton = findViewById(R.id.loginButton);
        loginReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // when clicked it will bring user back to the login page
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });
    }

    private void connectToFirebase() {
        // Initialize Firebase instances
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("User");
        auth = FirebaseAuth.getInstance();
    }
}
