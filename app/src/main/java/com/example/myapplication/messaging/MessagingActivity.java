package com.example.myapplication.messaging;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessagingActivity extends AppCompatActivity {
    private DatabaseReference dbReference;
    private String userId;
    private String receiverId;
    private String chatPath;
    private MessageAdapter adapter;
    private RecyclerView messageRecyclerView;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        initializeFirebase();
        initializeUI();
        setupReceiverInfo();
        setupChatPathAndLoadMessages();
    }

    private void initializeFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        dbReference = FirebaseDatabase.getInstance().getReference().child("chats");
    }

    private void initializeUI() {
        editTextMessage = findViewById(R.id.editTextMessage);
        Button sendButton = findViewById(R.id.buttonSend);
        messageRecyclerView = findViewById(R.id.messageRecycler);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, userId);
        messageRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(MessagingActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference chatRef = dbReference.child(chatPath); // Use the determined chatPath
        Message message = new Message(userId, receiverId, messageText, System.currentTimeMillis());

        chatRef.push().setValue(message).addOnSuccessListener(aVoid -> {
            editTextMessage.setText("");
            messageRecyclerView.scrollToPosition(messageList.size() - 1);
        }).addOnFailureListener(e -> Toast.makeText(MessagingActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show());
    }

    private void setupReceiverInfo() {
        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(receiverId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                toolbar.setTitle(fullName); // Set toolbar title
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessagingActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChatPathAndLoadMessages() {
        String path1 = userId + "_" + receiverId;
        String path2 = receiverId + "_" + userId;

        DatabaseReference path1Ref = dbReference.child(path1);
        path1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatPath = dataSnapshot.exists() ? path1 : path2; // Determine and store the chat path
                loadMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessagingActivity.this, "Error loading chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        DatabaseReference chatRef = dbReference.child(chatPath);
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    adapter.notifyDataSetChanged();
                    messageRecyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            // Implement other ChildEventListener methods as needed
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessagingActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu); // Inflate the options menu from XML
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showPopupMenu();
            return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_settings));
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu()); // Inflate your popup menu

        // Set an item click listener for the popup menu
        popupMenu.setOnMenuItemClickListener(item -> {
            Intent userProfileIntent = new Intent(MessagingActivity.this, UserProfile.class);
            userProfileIntent.putExtra("uid", receiverId);
            startActivity(userProfileIntent);
            return true;
        });

        popupMenu.show();
    }

    // This method sends a request to the provider
    public void sendRequest(Message message){
        String chatPath = userId + "_" + receiverId;

        DatabaseReference chatRef = dbReference.child(chatPath);

        chatRef.push().setValue(message)
                .addOnSuccessListener(aVoid -> messageRecyclerView.scrollToPosition(messageList.size() -1))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Could not send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }


}
