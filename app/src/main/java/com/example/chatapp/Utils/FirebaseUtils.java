package com.example.chatapp.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
}
