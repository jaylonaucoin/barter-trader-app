package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button passwordRecoverButton = findViewById(R.id.passwordRecoverButton);
        passwordRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event and navigate to the PasswordRecovery activity.
                Intent passwordRecoveryIntent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
                startActivity(passwordRecoveryIntent);
            }
        });
    }
}
