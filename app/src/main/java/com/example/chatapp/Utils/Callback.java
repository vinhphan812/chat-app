package com.example.chatapp.Utils;

import androidx.annotation.NonNull;

import com.example.chatapp.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Callback {
    public void call(@NonNull Task<AuthResult> task) {

    }

    public void call(Object list) {
    }

    public void callVoid(@NonNull Task<Void> task) {
    }

    public void call() {
    }

    public void call(User user) {
    }

    public void onError(Exception error) {

    }
}
