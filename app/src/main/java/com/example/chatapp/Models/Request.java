package com.example.chatapp.Models;

public class Request {
    public String userId;
    public Type type;
    enum Type{
        ADD_FRIEND, ADD_GROUP
    }
}
