package com.example.myapplication.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.PopupMenu;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MessagingActivity extends AppCompatActivity {
    private DatabaseReference dbReference;
    private String userId;
    private String receiverId; // Receiver's UID
    private MessageAdapter adapter;
    private RecyclerView messageRecyclerView;
    private List<Message> messageList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        initializeFirebase();
        initializeUI();
        setupReceiverInfo();
        getMessages();
    }

    private void initializeFirebase() {
        FirebaseAuth dbAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("chats");
        userId = Objects.requireNonNull(dbAuth.getCurrentUser()).getUid();
    }

    private void initializeUI() {
        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button sendButton = findViewById(R.id.buttonSend);
        messageRecyclerView = findViewById(R.id.messageRecycler);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, userId);
        messageRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void setupToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    private void sendMessage() {
        EditText input = findViewById(R.id.editTextMessage);
        String messageText = input.getText().toString();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String chatPath = userId + "_" + receiverId;
        DatabaseReference chatRef = dbReference.child(chatPath);

        Message message = new Message(userId, receiverId, messageText, System.currentTimeMillis());

        chatRef.push().setValue(message)
                .addOnSuccessListener(aVoid -> messageRecyclerView.scrollToPosition(messageList.size() - 1))
                .addOnFailureListener(e -> Toast.makeText(this, "Could not send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        input.setText("");
    }

    private void setupReceiverInfo() {
        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        if (receiverId == null) {
            receiverId = "default_receiver_id"; // Fallback ID or handle error
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(receiverId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String fullName = (firstName != null && lastName != null) ? firstName + " " + lastName : "Unknown";
                setupToolbarTitle(fullName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessagingActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMessages() {
        // Clear the list to ensure it starts fresh
        messageList.clear();

        // Assuming 'userId' is your current user's ID and 'receiverId' is the ID of the user they are chatting with
        String chatPath = userId + "_" + receiverId;
        DatabaseReference chatRef = dbReference.child(chatPath);

        // Attach a listener to read the data at our chat reference
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // A new message was added to the messages list
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    displayMessages(); // Call a method to handle the display of new messages
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Existing messages might be changed, for example, to mark them as read
                // You can implement logic here to handle such changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Messages might be deleted
                // You can implement logic here to handle message deletion if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Messages might be moved
                // Implement if your app logic requires handling message moving
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Database error occurred such as permission denied, handle the error
                Toast.makeText(MessagingActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
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
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);
        }
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable icon = menuItem.getIcon();
            if (icon != null) {
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }
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
        PopupMenu popupMenu = new PopupMenu(MessagingActivity.this, findViewById(R.id.action_settings));
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            // Pass the receiver UID to the UserProfile activity
            Intent profileIntent = new Intent(MessagingActivity.this, UserProfile.class);
            profileIntent.putExtra("uid", receiverId);
            startActivity(profileIntent);
            return true;
        });
        popupMenu.show();
    }
}
