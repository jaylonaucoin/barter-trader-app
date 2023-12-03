package com.example.myapplication.registration;

import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.function.Consumer;

public class FirebaseRegistrationService {

    private final FirebaseAuth auth;
    private final FirebaseDatabase firebaseDB;
    private final Consumer<String> onFailure;
    private final Runnable onSuccess;

    public FirebaseRegistrationService(Runnable onSuccess, Consumer<String> onFailure) {
        this.auth = FirebaseAuth.getInstance();
        this.firebaseDB = FirebaseDatabase.getInstance();
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public void registerUser(String email, String password, String confirmPassword,
                             String fname, String lname, String role) {


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)
                || TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(role)) {
            onFailure.accept("Please fill out all fields!");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onFailure.accept("Please enter a valid email address!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            onFailure.accept("Passwords do not match!");
            return;
        }

        if (password.length() < 6) {
            onFailure.accept("Password must be at least 6 characters!");
            return;
        }

        // Firebase registration process
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference userRef = firebaseDB.getReference("User/" + user.getUid());
                            userRef.child("firstName").setValue(fname);
                            userRef.child("lastName").setValue(lname);
                            userRef.child("role").setValue(role);
                            onSuccess.run();
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            onFailure.accept("Email is already in use!");
                        } else {
                            onFailure.accept("Unknown error occurred. Please try again.");
                        }
                    }
                });
    }
}
