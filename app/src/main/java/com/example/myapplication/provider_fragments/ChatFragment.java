package com.example.myapplication.provider_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.provider_chat, container, false);

        RecyclerView chatMessagesRecyclerView = view.findViewById(R.id.chatMessagesRecyclerView);
        chatMessagesRecyclerView.setHasFixedSize(true);
        chatMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatMessagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatMessagesList);
        chatMessagesRecyclerView.setAdapter(chatAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadChats();

        return view;
    }

    private void loadChats() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            // Adjust the query as per your database structure
            mDatabase.child("chats").orderByKey().startAt(currentUserId).endAt(currentUserId + "\uf8ff")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            chatMessagesList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String receiverId = Objects.requireNonNull(snapshot.getKey()).split("_")[1];
                                fetchMostRecentMessage(snapshot, receiverId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle cancelled event
                        }
                    });
        }
    }

    private void fetchMostRecentMessage(DataSnapshot chatSnapshot, String receiverId) {
        ChatMessage mostRecentMessage = null;
        long highestTimestamp = 0;
        for (DataSnapshot messageSnapshot : chatSnapshot.getChildren()) {
            Long timestampObject = messageSnapshot.child("timestamp").getValue(Long.class);
            if (timestampObject != null) {
                long timestamp = timestampObject;
                if (timestamp > highestTimestamp) {
                    highestTimestamp = timestamp;
                    String text = messageSnapshot.child("text").getValue(String.class);
                    mostRecentMessage = new ChatMessage(receiverId, messageSnapshot.child("senderId").getValue(String.class), text, timestamp);
                }
            }
        }
        if (mostRecentMessage != null) {
            fetchUserDetails(mostRecentMessage);
        }
    }

    private void fetchUserDetails(ChatMessage mostRecentMessage) {
        mDatabase.child("User").child(mostRecentMessage.getReceiverId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                        String fullName = firstName + " " + lastName;
                        mostRecentMessage.setSenderId(fullName);
                        chatMessagesList.add(mostRecentMessage);
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle cancelled event
                    }
                });
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        private final List<ChatMessage> chatMessages;
        private final Context context;

        ChatAdapter(Context context, List<ChatMessage> chatMessages) {
            this.context = context;
            this.chatMessages = chatMessages;
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView profileIcon;
            TextView username;
            TextView recentMessage;

            ViewHolder(View itemView) {
                super(itemView);
                profileIcon = itemView.findViewById(R.id.imageViewProfileIcon);
                username = itemView.findViewById(R.id.textViewUsername);
                recentMessage = itemView.findViewById(R.id.textViewRecentMessage);
            }

            void bind(Context context, ChatMessage message) {
                username.setText(message.getSenderId());
                recentMessage.setText(message.getText());
                profileIcon.setImageResource(R.drawable.empty_user_icon);

                profileIcon.setOnClickListener(v -> {
                    Intent profileIntent = new Intent(context, UserProfile.class);
                    profileIntent.putExtra("uid", message.getReceiverId());
                    context.startActivity(profileIntent);
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage message = chatMessages.get(position);
            holder.bind(context, message);
            holder.recentMessage.setText(message.getText());
            // Set an icon for the user
            holder.profileIcon.setImageResource(R.drawable.empty_user_icon);
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }
    }
}
