package com.example.myapplication.receiver_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.messaging.MessagingActivity;
import com.example.myapplication.provider_fragments.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReceiverChatFragment extends Fragment {

    private ChatAdapter chatAdapter;
    private final ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
    private DatabaseReference chatsRef;
    private DatabaseReference usersRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.receiver_chat, container, false);
        RecyclerView chatMessagesRecyclerView = view.findViewById(R.id.chatMessagesRecyclerView);
        chatMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("chats");
        usersRef = FirebaseDatabase.getInstance().getReference().child("User");

        chatAdapter = new ChatAdapter(chatMessagesList);
        chatMessagesRecyclerView.setAdapter(chatAdapter);

        // Load chat messages
        loadChatMessages(list -> chatAdapter.notifyDataSetChanged());

        // Set click listener for chat items
        chatAdapter.setOnChatListener(partnerId -> {
            Intent intent = new Intent(getActivity(), MessagingActivity.class);
            intent.putExtra("RECEIVER_ID", partnerId);
            startActivity(intent);
        });

        return view;
    }

    // Load chat messages from Firebase
    private void loadChatMessages(FirebaseCallback callback) {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatKey = snapshot.getKey();
                    if (chatKey != null && chatKey.contains(currentUserId)) {
                        String[] userIds = chatKey.split("_");
                        String partnerId = userIds[0].equals(currentUserId) ? userIds[1] : userIds[0];

                        ChatMessage recentMessage = getMostRecentMessage(snapshot, partnerId);
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

    // Get the most recent message from a chat
    private ChatMessage getMostRecentMessage(DataSnapshot chatSnapshot, String partnerId) {
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

    public interface FirebaseCallback {
        void onCallback(List<ChatMessage> list);
    }

    public static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        private final List<ChatMessage> chatMessages;
        private OnChatListener onChatListener;

        public ChatAdapter(List<ChatMessage> chatMessages) {
            this.chatMessages = chatMessages;
        }

        public interface OnChatListener {
            void onChatSelected(String partnerId);
        }

        public void setOnChatListener(OnChatListener onChatListener) {
            this.onChatListener = onChatListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage chatMessage = chatMessages.get(position);
            holder.textViewUsername.setText(chatMessage.getFullName());
            holder.textViewRecentMessage.setText(chatMessage.getText());

            holder.itemView.setOnClickListener(v -> {
                if (onChatListener != null) {
                    onChatListener.onChatSelected(chatMessage.getReceiverId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewUsername;
            TextView textViewRecentMessage;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewUsername = itemView.findViewById(R.id.textViewUsername);
                textViewRecentMessage = itemView.findViewById(R.id.textViewRecentMessage);
            }
        }
    }
}
