package com.example.myapplication.registration;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PasswordRecoveryActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        connectToFirebase();

        TextView errorMessageTextView = findViewById(R.id.errorMessage);

        // grabbing the reset password button
        Button resetPasswordButton = findViewById(R.id.sendResetLinkButton);
        EditText emailEditText = findViewById(R.id.emailEditText);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click for when reset button is clicked
            public void onClick(View v) {
                // grabbing the user inputted email
                String userEmail = emailEditText.getText().toString();
                // checking if the email field has been filled
                if (!TextUtils.isEmpty(userEmail)) {
                    // calling the method to send the reset button
                    sendPasswordResetEmail(userEmail);
                    emailEditText.setText("");
                }
                // if email is empty display toast
                else {
                    errorMessageTextView.setText("Please enter your email address");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        // grabbing the return to login page button
        Button loginReturnButton = findViewById(R.id.loginReturnButton);
        loginReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // when clicked it will bring user back to the login page
            public void onClick(View v) {
                Intent loginIntent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });
    }

    // method for a user to reset their password
    private void sendPasswordResetEmail(String email) {
        // uses firebase pre-made method
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email to reset password was sent
                            Toast.makeText(PasswordRecoveryActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // email was not able to be sent
                            Toast.makeText(PasswordRecoveryActivity.this, "Password reset email not sent. Check your email address.", Toast.LENGTH_SHORT).show();
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
