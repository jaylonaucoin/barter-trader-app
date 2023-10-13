package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        connectToFirebase();

        // retrieving the button to recover password
        Button passwordRecoverButton = findViewById(R.id.passwordRecoverButton);
        passwordRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when the forgot password is clicked
            public void onClick(View v) {
                // handle the click event and navigate to the PasswordRecovery activity
                Intent passwordRecoveryIntent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
                startActivity(passwordRecoveryIntent);
            }
        });

        // retrieving the button to login a user
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // getting the users inputted email and password
                EditText emailEditText = findViewById(R.id.email);
                EditText passwordEditText = findViewById(R.id.password);
                String userEmail = emailEditText.getText().toString();
                String userPassword = passwordEditText.getText().toString();

                // attempting to login with provided info using the auth methods
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword);
            }
        });
    }

    private void connectToFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("test");
    }
}
