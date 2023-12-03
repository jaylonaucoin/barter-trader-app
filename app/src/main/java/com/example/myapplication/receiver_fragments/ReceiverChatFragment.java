package com.example.myapplication.receiver_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.messaging.MessagingActivity;
import com.example.myapplication.provider_fragments.ChatAdapter;
import com.example.myapplication.provider_fragments.ChatDataManager;
import com.example.myapplication.provider_fragments.FirebaseChatService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class ReceiverChatFragment extends Fragment {

    private ChatAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.receiver_chat, container, false);
        RecyclerView chatMessagesRecyclerView = view.findViewById(R.id.chatMessagesRecyclerView);
        chatMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        chatAdapter = new ChatAdapter(new ArrayList<>());
        chatMessagesRecyclerView.setAdapter(chatAdapter);

        ChatDataManager chatDataManager = new ChatDataManager(new FirebaseChatService());
        chatDataManager.loadChatMessages(currentUserId, list -> {
            chatAdapter.setChatMessages(list);
            chatAdapter.notifyDataSetChanged();
        });

        chatAdapter.setOnChatListener(partnerId -> {
            Intent intent = new Intent(getActivity(), MessagingActivity.class);
            intent.putExtra("RECEIVER_ID", partnerId);
            startActivity(intent);
        });

        return view;
    }

}
