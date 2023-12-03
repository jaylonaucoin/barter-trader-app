package com.example.myapplication.registration;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseLoginService firebaseLoginService;
    private TextView errorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseLoginService = new FirebaseLoginService(this::onLoginSuccess, this::onLoginFailure);

        errorMessageTextView = findViewById(R.id.errorMessage);

        setupPasswordRecoveryButton();
        setupRegisterButton();
        setupLoginButton();
    }

    private void setupPasswordRecoveryButton() {
        Button passwordRecoverButton = findViewById(R.id.passwordRecoverButton);
        passwordRecoverButton.setOnClickListener(v -> navigateTo(PasswordRecoveryActivity.class));
    }

    private void setupRegisterButton() {
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> navigateTo(RegisterActivity.class));
    }

    private void setupLoginButton() {
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        String userEmail = emailEditText.getText().toString();
        String userPassword = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            displayErrorMessage("Both email and password are required");
        } else {
            firebaseLoginService.signIn(userEmail, userPassword);
        }
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(LoginActivity.this, cls);
        startActivity(intent);
    }

    private void displayErrorMessage(String message) {
        errorMessageTextView.setText(message);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void onLoginSuccess() {
        errorMessageTextView.setVisibility(View.GONE);
        navigateTo(FirstLoginLocation.class);
    }

    private void onLoginFailure() {
        displayErrorMessage("Login failed. Check your credentials");
    }
}
