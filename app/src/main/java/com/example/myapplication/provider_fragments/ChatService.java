package com.example.myapplication.provider_fragments;


public interface ChatService {
    void loadChatMessages(String userId, ChatDataManager.FirebaseCallback callback);
}
