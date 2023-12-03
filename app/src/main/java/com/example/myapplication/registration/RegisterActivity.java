package com.example.myapplication.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseRegistrationService firebaseRegistrationService;
    private TextView errorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseRegistrationService = new FirebaseRegistrationService(this::onRegistrationSuccess, this::onRegistrationFailure);

        errorMessageTextView = findViewById(R.id.errorMessage);

        setupRegisterButton();
        setupLoginReturnButton();
    }

    private void setupRegisterButton() {
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirmPassword);
        EditText fname = findViewById(R.id.firstName);
        EditText lname = findViewById(R.id.lastName);
        Spinner role = findViewById(R.id.role);

        firebaseRegistrationService.registerUser(
                email.getText().toString(),
                password.getText().toString(),
                confirmPassword.getText().toString(),
                fname.getText().toString(),
                lname.getText().toString(),
                role.getSelectedItem().toString()
        );
    }

    private void setupLoginReturnButton() {
        Button loginReturnButton = findViewById(R.id.loginButton);
        loginReturnButton.setOnClickListener(v -> navigateTo(LoginActivity.class));
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(RegisterActivity.this, cls);
        startActivity(intent);
    }

    private void onRegistrationSuccess() {
        Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
        navigateTo(LoginActivity.class);
    }

    private void onRegistrationFailure(String errorMessage) {
        errorMessageTextView.setText(errorMessage);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }
}
