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
    private final String receiverId = "8VZgWEx1PYgn9zdfqkHhPloCsop1"; //Hardcoded has to be the persons UID
    private MessageAdapter adapter;
    private RecyclerView messageRecyclerView;
    private List<Message> messageList;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_settings) {
            PopupMenu popupMenu = new PopupMenu(MessagingActivity.this, findViewById(R.id.action_settings));

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                Intent intent = new Intent(MessagingActivity.this, UserProfile.class);
                startActivity(intent);
                return true;
            });

            popupMenu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Button sendButton = findViewById(R.id.buttonSend);

        setOptionsMenu();

        FirebaseAuth dbAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("chats");

        userId = Objects.requireNonNull(dbAuth.getCurrentUser()).getUid();
        messageRecyclerView = findViewById(R.id.messageRecycler);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, userId);
        messageRecyclerView.setAdapter(adapter);
        messageRecyclerView.scrollToPosition(messageList.size() -1);

        setButtonListener(sendButton);
        getMessages();

    }

    private void setOptionsMenu(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Christopher Hage"); //Hardcoded has to be the persons name
        Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);
        }
        setSupportActionBar(toolbar);
    }
    private void setButtonListener(Button sendButton){
        sendButton.setOnClickListener(v -> {
            EditText input = findViewById(R.id.editTextMessage);
            if (input.getText().toString().isEmpty()){
                return;
            }

            String chatPath = userId + "_" + receiverId;

            DatabaseReference chatRef = dbReference.child(chatPath);

            Message message = new Message(userId, receiverId, input.getText().toString(),System.currentTimeMillis() );

            chatRef.push().setValue(message)
                    .addOnSuccessListener(aVoid -> messageRecyclerView.scrollToPosition(messageList.size() -1))
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Could not send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            input.setText("");

        });
    }
    // This method sends a request to the provider
    public void sendRequest(Message message){
        String chatPath = userId + "_" + receiverId;

        DatabaseReference chatRef = dbReference.child(chatPath);

        chatRef.push().setValue(message)
                .addOnSuccessListener(aVoid -> messageRecyclerView.scrollToPosition(messageList.size() -1))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Could not send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }
    private void getMessages() {
        messageList.clear();

        String senderPath = userId + "_" + receiverId;
        String receiverPath = receiverId + "_" + userId;
        DatabaseReference senderRef = dbReference.child(senderPath);
        DatabaseReference receiverRef = dbReference.child(receiverPath);

        senderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                displayMessages(messageList);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        receiverRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messageList.add(message);
                displayMessages(messageList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });


    }

    private void displayMessages(List<Message> messageList) {
        messageList.sort(Comparator.comparingLong(Message::getTimestamp));
        messageRecyclerView.scrollToPosition(messageList.size()-1);
        adapter.notifyDataSetChanged();

    }

}