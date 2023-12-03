package com.example.myapplication.provider_fragments;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FirebaseChatService implements ChatService {

    private final DatabaseReference chatsRef;
    private final DatabaseReference usersRef;

    public FirebaseChatService() {
        chatsRef = FirebaseDatabase.getInstance().getReference().child("chats");
        usersRef = FirebaseDatabase.getInstance().getReference().child("User");
    }

    @Override
    public void loadChatMessages(String userId, ChatDataManager.FirebaseCallback callback) {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatMessage> chatMessagesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatKey = snapshot.getKey();
                    if (chatKey != null && chatKey.contains(userId)) {
                        String[] userIds = chatKey.split("_");
                        String partnerId = userIds[0].equals(userId) ? userIds[1] : userIds[0];

                        ChatMessage recentMessage = getMostRecentMessage(snapshot, partnerId, userId);
                        if (recentMessage != null) {
                            usersRef.child(partnerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    String firstName = userSnapshot.child("firstName").getValue(String.class);
                                    String lastName = userSnapshot.child("lastName").getValue(String.class);
                                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");

                                    recentMessage.setFullName(fullName);
                                    chatMessagesList.add(recentMessage);
                                    callback.onCallback(chatMessagesList);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle error
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private ChatMessage getMostRecentMessage(DataSnapshot chatSnapshot, String partnerId, String currentUserId) {
        ChatMessage recentMessage = null;
        long maxTimestamp = 0;
        for (DataSnapshot messageSnapshot : chatSnapshot.getChildren()) {
            Long timestampLong = messageSnapshot.child("timestamp").getValue(Long.class);
            if (timestampLong != null) {
                long timestamp = timestampLong;
                if (timestamp > maxTimestamp) {
                    maxTimestamp = timestamp;
                    String text = messageSnapshot.child("text").getValue(String.class);
                    recentMessage = new ChatMessage(partnerId, currentUserId, text, timestamp);
                }
            }
        }
        return recentMessage;
    }
}
