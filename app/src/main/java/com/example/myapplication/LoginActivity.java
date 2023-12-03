package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.registration.FirstLoginLocation;
import com.example.myapplication.registration.PasswordRecoveryActivity;
import com.example.myapplication.registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText emailEditText, passwordEditText;
    private TextView errorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeFirebase();
        initializeViews();
        setupButtonListeners();
    }

    // Initialize Firebase related objects
    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Initialize UI components
    private void initializeViews() {
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        errorMessageTextView = findViewById(R.id.errorMessage);
    }

    // Set up listeners for buttons
    private void setupButtonListeners() {
        findViewById(R.id.passwordRecoverButton).setOnClickListener(v -> openPasswordRecovery());
        findViewById(R.id.registerButton).setOnClickListener(v -> openRegisterPage());
        findViewById(R.id.loginButton).setOnClickListener(v -> attemptLogin());
    }

    // Open Password Recovery Activity
    private void openPasswordRecovery() {
        startActivity(new Intent(this, PasswordRecoveryActivity.class));
    }

    // Open Register Activity
    private void openRegisterPage() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // Attempt to log in the user
    private void attemptLogin() {
        String userEmail = emailEditText.getText().toString();
        String userPassword = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            errorMessageTextView.setText("Both email and password are required");
            errorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            performFirebaseLogin(userEmail, userPassword);
        }
    }

    // Perform Firebase Authentication
    private void performFirebaseLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        errorMessageTextView.setVisibility(View.GONE);
                        startActivity(new Intent(this, FirstLoginLocation.class));
                    } else {
                        errorMessageTextView.setText("Login failed. Check your credentials");
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
