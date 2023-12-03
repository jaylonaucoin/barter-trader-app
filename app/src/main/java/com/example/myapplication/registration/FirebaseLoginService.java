package com.example.myapplication.registration;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseLoginService {

    private final FirebaseAuth firebaseAuth;
    private final Runnable onSuccess;
    private final Runnable onFailure;

    public FirebaseLoginService(Runnable onSuccess, Runnable onFailure) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        onFailure.run();
                    }
                });
    }
}
