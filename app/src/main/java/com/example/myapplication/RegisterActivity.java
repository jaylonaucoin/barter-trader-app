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
                final String[] errorMessage = {""};

                boolean allDataEntered = !passwordEntered.isEmpty() && !confirmPasswordEntered.isEmpty()
                        && !emailEntered.isEmpty() && !fnameEntered.isEmpty() && !lnameEntered.isEmpty()
                        && !roleEntered.isEmpty();

                if (allDataEntered) {
                    if(Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches()) {
                        if(passwordEntered.equals(confirmPasswordEntered)) {
                            if(passwordEntered.length() >= 6) {
                                auth.createUserWithEmailAndPassword(emailEntered, passwordEntered)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = auth.getCurrentUser();
                                                    if (user != null) {
                                                        DatabaseReference userRef = firebaseDB.getReference("User/" + user.getUid());
                                                        userRef.child("firstName").setValue(fnameEntered);
                                                        userRef.child("lastName").setValue(lnameEntered);
                                                        userRef.child("role").setValue(roleEntered);

                                                        Intent intent = new Intent(RegisterActivity.this, RegisterSuccessActivity.class);
                                                        intent.putExtra("name", fnameEntered);
                                                        startActivity(intent);
                                                    }
                                                } else {
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
                if(!errorMessage[0].isEmpty()) {
                    Toast.makeText(getApplicationContext(), errorMessage[0], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connectToFirebase() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("User");
        auth = FirebaseAuth.getInstance();
    }
}
