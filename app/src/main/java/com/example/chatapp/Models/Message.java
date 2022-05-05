package com.example.chatapp.Models;

import java.util.Date;

public class Message {
    public String messageId;
    public String sender;
    public String message;
    public Date send_at;

    public Message(String sender, String message, Date send_at) {
        this.sender = sender;
        this.message = message;
        this.send_at = send_at;
    }

    public Message(String messageId, String sender, String message, Date send_at) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
        this.send_at = send_at;
    }
}
