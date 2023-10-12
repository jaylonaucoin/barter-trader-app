package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordRecoveryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Button loginReturnButton = findViewById(R.id.loginReturnButton);
        loginReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event and navigate back to the LoginActivity.
                Intent loginIntent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

    }
}
