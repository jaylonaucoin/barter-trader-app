package com.example.myapplication.messaging;

public class Message {
    private final String senderId;
    private final String receiverId;
    private final String text;
    private final long timestamp;

    public Message() {
        senderId = "";
        receiverId = "";
        text = "";
        timestamp = 0;
    }

    public Message(String senderId, String receiverId, String text, long timestamp) {
        if (senderId == null || receiverId == null || text == null) {
            throw new IllegalArgumentException("Sender ID, Receiver ID, and Text cannot be null");
        }

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
