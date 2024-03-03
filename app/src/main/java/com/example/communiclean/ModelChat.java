package com.example.communiclean;

public class ModelChat {
    String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    String receiver;

    public ModelChat() {
    }

    String sender;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ModelChat(String message, String receiver, String sender, String type, String timestamp, boolean seen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    String type;


    String timestamp;


    boolean seen;
}