package com.example.myapplication.provider_fragments;

public class ChatMessage {
    private final String receiverId;
    private String senderId;
    private final String text;
    private final long timestamp;
    private String fullName; // New property for full name

    // Constructor
    public ChatMessage(String receiverId, String senderId, String text, long timestamp) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getReceiverId() { return receiverId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
