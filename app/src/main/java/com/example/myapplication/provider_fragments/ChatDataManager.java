package com.example.myapplication.provider_fragments;

import java.util.List;

public class ChatDataManager {

    private final ChatService chatService;

    public ChatDataManager(ChatService chatService) {
        this.chatService = chatService;
    }

    public void loadChatMessages(String currentUserId, FirebaseCallback callback) {
        chatService.loadChatMessages(currentUserId, callback);
    }

    public interface FirebaseCallback {
        void onCallback(List<ChatMessage> list);
    }
}
