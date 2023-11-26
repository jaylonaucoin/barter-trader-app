package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.notifications.NotificationWorker;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth firebaseAuth;


    public void scheduleWorker(String token) {

        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.HOURS);
        Data data = new Data.Builder()
                .putString("token", token)
                .build();

        PeriodicWorkRequest hourlyWork = workBuilder.setInputData(data).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("test", ExistingPeriodicWorkPolicy.KEEP, hourlyWork);

    }

    public void startWorkerProcess() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult();
                        scheduleWorker(token);

                    }
                });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_refactor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView userProfileIcon = findViewById(R.id.icon_user_profile);
        userProfileIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        ImageView addressIcon = findViewById(R.id.icon_address);
        addressIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.white));

        userProfileIcon.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, UserProfile.class);
            startActivity(intent);
        });

        addressIcon.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, SavedAddresses.class);
            startActivity(intent);
        });

        /*
        connectToFirebase();
        startWorkerProcess();


        Button loginPageButton = findViewById(R.id.loginPage);
        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // when clicked bring user to login page
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });


        // grabbing the login button
        Button registerPageButton = findViewById(R.id.registerPage);
        registerPageButton.setOnClickListener(new View.OnClickListener() {
            @Override

            // when clicked bring user to login page
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    protected void connectToFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDBRef = firebaseDB.getReference("test");
    }
    }

}
*/
    }
}

