package com.example.myapplication.provider_fragments;

public class ChatMessage {
    private final String receiverId;
    private String senderId;
    private final String text;
    private final long timestamp;

    public ChatMessage(String receiverId, String senderId, String text, long timestamp) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

