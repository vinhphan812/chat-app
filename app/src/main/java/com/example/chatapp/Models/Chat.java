package com.example.chatapp.Models;

public class Chat {
    public String id;
    public String code;
    public Boolean isDeleted;
    public String name;
    public String created_by;
    public Message last_message;

    public Chat(String name, Message last_message) {
        this.name = name;
        this.last_message = last_message;
    }

    public Chat(String id, String code, Boolean isDeleted, String name, String created_by, Message last_message) {
        this.id = id;
        this.code = code;
        this.isDeleted = isDeleted;
        this.name = name;
        this.created_by = created_by;
        this.last_message = last_message;
    }
}
