package com.example.chatapp.Utils;

import androidx.annotation.NonNull;

import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Services {
    // region property
    private static final int CODE_LENGTH = 10;
    private static String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // endregion

    // region FirebaseUtils
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    // endregion

    // region Enum
    enum RequestType {
        ADD_FRIEND, ADD_GROUP
    }

    enum FirebaseKey {
        Users, Chats, UserInChat, MessagesInChats
    }
    // endregion

    //region Firebase

    // region User
    public static Boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public static FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public static String getUserID() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public static void registerAccount(String email, String password, HashMap<String, Object> userInfo, Callback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            callback.call(task);
            if (!task.isSuccessful()) {
                callback.onError(task.getException());
                return;
            }

            database.child(FirebaseKey.Users.name()).child(Services.getUserID()).setValue(userInfo).addOnCompleteListener(infoTask -> {
                if (!infoTask.isSuccessful()) {
                    callback.onError(infoTask.getException());
                }
                callback.call();
            });

        });
    }

    public static void getUserInfo(Callback callback) {
        String userID = getUserID();
        database.child(FirebaseKey.Users.name()).child(userID).get().addOnSuccessListener(snapshot -> {
            User user = snapshot.getValue(User.class);
            callback.call(user);
        });

    }

    public static void updateUserInfo(HashMap<String, Object> info, Callback callback) {
        String userID = getUserID();
        database.child(FirebaseKey.Users.name()).child(userID).updateChildren(info).addOnCompleteListener(task -> callback.callVoid(task));
    }

    public static void Login(String user, String pass, Callback callback) {
        mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(task -> callback.call(task));
    }

    public static void Logout(Callback callback) {
        mAuth.signOut();
        callback.call();
    }
    // endregion

    public static void createGroup(String name, Callback callback) {
        HashMap<String, Object> chat = new HashMap<>();

        String code = makeCode();
        String userID = getUserID();


        chat.put("name", name);
        chat.put("code", code);
        chat.put("created_by", userID);

        database.child(FirebaseKey.Chats.name()).child(code).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    callback.onError(task.getException());
                    return;
                }

                List<String> userInChat = new ArrayList<>();
                userInChat.add(code);

                database.child(FirebaseKey.UserInChat.name()).child(userID);

                database.child(FirebaseKey.UserInChat.name()).child(userID).setValue(userInChat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            callback.onError(task.getException());
                            return;
                        }
                        callback.call();
                    }
                });
            }
        });
    }

    public static void getAllGroup(Callback callback) {
        String userID = getUserID();

        database.child(FirebaseKey.UserInChat.name()).child(userID).get().addOnSuccessListener(dataSnapshot -> {
            List<String> list = (List<String>) dataSnapshot.getValue();

            if (list == null) list = new ArrayList<>();

            List<Chat> chats = new ArrayList<>();
            for (String id : list) {
                chats.add(getChat(id));
            }
            callback.call(chats);
        });
    }

    private static Message getLastMessage(String chatID) {
        Task<DataSnapshot> task = database.child(FirebaseKey.MessagesInChats.name()).child(chatID).limitToFirst(1).get();
        while (!task.isComplete()) {
        }

        DataSnapshot snapshot = task.getResult();

        return snapshot.getValue(Message.class);
    }

    public static Chat getChat(String id) {
        Task<DataSnapshot> task = database.child(FirebaseKey.Chats.name()).child(id).get();
        while (!task.isComplete()) {

        }
        DataSnapshot snapshot = task.getResult();
        Chat chat = snapshot.getValue(Chat.class);

        chat.last_message = getLastMessage(id);

        return chat;
    }

    public static Boolean sendRequest(String id, RequestType type) {
        return true;
    }

    public static void JoinGroup(String code) {
    }

    public static void acceptRequest(String id, RequestType type) {
    }

    public static void sendMessage(String chatId, String message) {
    }

    public static void removeMessage(String chatId, String messageId) {
    }

    public static void getMessage(String chatId) {

    }
    // endregion

    // region Local Function
    private static String makeCode() {
        String code = "";
        Random rnd = new Random();
        String[] characterArray = characters.split("");
        for (int i = 0; i < CODE_LENGTH; i++) {
            code += characterArray[rnd.nextInt(characters.length())];
        }
        return code;
    }
    // endregion
}
