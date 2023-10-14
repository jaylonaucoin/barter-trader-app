package com.example.myapplication.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MessagingActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private FirebaseDatabase db;
    private DatabaseReference dbReference;
    private String userId;
    private String receiverId = "8VZgWEx1PYgn9zdfqkHhPloCsop1"; //Hardcoded has to be the persons UID
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
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(MessagingActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
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

        dbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
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
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            messageRecyclerView.scrollToPosition(messageList.size() -1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Could not send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            input.setText("");

        });
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
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });


    }

    private void displayMessages(List<Message> messageList) {
        messageList.sort(new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Long.compare(m1.getTimestamp(), m2.getTimestamp());
            }

        });
        messageRecyclerView.scrollToPosition(messageList.size()-1);
        adapter.notifyDataSetChanged();

    }

}