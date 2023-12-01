package com.example.myapplication.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MessagingActivity extends AppCompatActivity {
    private DatabaseReference dbReference;
    private String userId;
    private static final String RECEIVER_ID = "8VZgWEx1PYgn9zdfqkHhPloCsop1"; // Person's UID
    private MessageAdapter adapter;
    private RecyclerView messageRecyclerView;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initializeFirebase();
        initializeUI();
        getMessages();
    }

    private void initializeFirebase() {
        FirebaseAuth dbAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("chats");
        userId = Objects.requireNonNull(dbAuth.getCurrentUser()).getUid();
    }

    private void initializeUI() {
        Toolbar toolbar = setupToolbar();
        Button sendButton = findViewById(R.id.buttonSend);
        messageRecyclerView = setupRecyclerView();

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, userId);
        messageRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private Toolbar setupToolbar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Christopher Hage"); // Person's name
        setSupportActionBar(toolbar);
        setupBackButton(toolbar);
        return toolbar;
    }

    private void setupBackButton(Toolbar toolbar) {
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);
        }
    }

    private RecyclerView setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.messageRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        return recyclerView;
    }

    private void sendMessage() {
        EditText input = findViewById(R.id.editTextMessage);
        String messageText = input.getText().toString();
        if (messageText.isEmpty()) {
            return;
        }

        String chatPath = userId + "_" + RECEIVER_ID;
        DatabaseReference chatRef = dbReference.child(chatPath);

        Message message = new Message(userId, RECEIVER_ID, messageText, System.currentTimeMillis());

        chatRef.push().setValue(message)
                .addOnSuccessListener(aVoid -> messageRecyclerView.scrollToPosition(messageList.size() - 1))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Could not send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        input.setText("");
    }

    private void getMessages() {
        messageList.clear();
        addChildEventListener(userId + "_" + RECEIVER_ID);
        addChildEventListener(RECEIVER_ID + "_" + userId);
    }

    private void addChildEventListener(String chatPath) {
        DatabaseReference chatRef = dbReference.child(chatPath);
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                displayMessages();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });
    }

    private void displayMessages() {
        messageList.sort(Comparator.comparingLong(Message::getTimestamp));
        adapter.notifyDataSetChanged();
        messageRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showPopupMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(MessagingActivity.this, findViewById(R.id.action_settings));
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(MessagingActivity.this, UserProfile.class));
            return true;
        });
        popupMenu.show();
    }
}
