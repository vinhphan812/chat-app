package com.example.chatapp.Utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Services {
    // region property
    private static final int CODE_LENGTH = 10;
    private static String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // endregion

    // region FirebaseUtils
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
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

    private static User getUserById(String id) {
        Task<DataSnapshot> task = database.child(FirebaseKey.Users.name()).child(id).get();

        while (!task.isComplete()) {
        }

        User user = task.getResult().getValue(User.class);

        return user;
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

            userInfo.put("userID", Services.getUserID());

            database.child(FirebaseKey.Users.name()).child(Services.getUserID()).setValue(userInfo).addOnCompleteListener(infoTask -> {
                if (!infoTask.isSuccessful()) {
                    callback.onError(infoTask.getException());
                }
                callback.call();
            });

        });
    }

    public static User getUserInfo() {
        String userID = getUserID();

        Task<DataSnapshot> task = database.child(FirebaseKey.Users.name()).child(userID).get();

        while (!task.isComplete()) {
        }

        return task.getResult().getValue(User.class);
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

        List<String> participants = new ArrayList<>();

        participants.add(userID);

        chat.put("name", name);
        chat.put("code", code);
        chat.put("created_by", userID);
        chat.put("participants", participants);

        database.child(FirebaseKey.Chats.name()).child(code).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    callback.onError(task.getException());
                    return;
                }

                List<String> chats = getChatOfUser();

                database.child(FirebaseKey.UserInChat.name()).child(userID).child((chats == null ? 0 : chats.size()) + "").setValue(code).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            callback.onError(task.getException());
                            return;
                        }
                        callback.call("Create Successful!");
                    }
                });
            }
        });
    }

    private static List<String> getChatOfUser() {
        String userID = getUserID();
        Task<DataSnapshot> task = database.child(FirebaseKey.UserInChat.name()).child(userID).get();
        while (!task.isComplete()) {
        }

        List<String> chats = (List<String>) task.getResult().getValue();

        return chats;
    }

    private static List<String> getParticipants(String chatId) {
        Task<DataSnapshot> task = database.child(FirebaseKey.Chats.name()).child(chatId).child("participants").get();

        while (!task.isComplete()) {
        }

        List<String> participants = (List<String>) task.getResult().getValue();

        return participants;
    }

    public static List<User> getParticipantsInChat(String chatId) {
        List<String> list = getParticipants(chatId);
        List<User> users = new ArrayList<>();

        for (String userId : list) {
            users.add(getUserById(userId));
        }

        return users;
    }

    public static void getParticipantsInChat(String chatId, Callback callback) {
        database.child(FirebaseKey.Chats.name()).child(chatId).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = (List<String>) snapshot.getValue();

                List<User> participants = new ArrayList<>();

                for (String id : list) {
                    participants.add(getUserById(id));
                }

                callback.call(participants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getAllGroup(Callback callback) {
        String userID = getUserID();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.child(FirebaseKey.UserInChat.name()).child(userID).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("Parse Error", task.getException().getMessage());
                        return;
                    }
                    List<String> list = (List<String>) task.getResult().getValue();

                    if (list == null) list = new ArrayList<>();

                    List<Chat> chats = new ArrayList<>();
                    for (String id : list) {
                        chats.add(getChat(id));
                    }
                    callback.call(chats);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static Message getLastMessage(String chatID) {
        Task<DataSnapshot> task = database.child(FirebaseKey.MessagesInChats.name()).child(chatID).limitToLast(1).get();
        while (!task.isComplete()) {
        }

        DataSnapshot snapshot = task.getResult();

        List<Message> messages = new ArrayList<>();

        Log.d("abc", snapshot.getChildrenCount() + "");

        if (snapshot.getChildrenCount() == 0) return null;

        for (DataSnapshot snap : snapshot.getChildren()) {
            messages.add(snap.getValue(Message.class));
        }

        return messages.get(0);
    }

    public static Chat getChat(String id) {
        Task<DataSnapshot> task = database.child(FirebaseKey.Chats.name()).child(id).get();
        while (!task.isComplete()) {

        }
        DataSnapshot snapshot = task.getResult();
        Chat chat = snapshot.getValue(Chat.class);

        Message last_message = getLastMessage(id);

        if (last_message != null) chat.last_message = last_message;

        return chat;
    }

    private static boolean isExistsInGroup(String chat) {
        Task<DataSnapshot> snap = database.child(FirebaseKey.UserInChat.name()).child(getUserID()).get();
        while (!snap.isComplete()) {
        }

        List<String> list = (List<String>) snap.getResult().getValue();

        if (list == null) return false;

        for (String id : list) {
            if (chat.equals(id)) return true;
        }
        return false;
    }

    public static void JoinGroup(String code, Callback callback) {
        String userID = getUserID();
        database.child(FirebaseKey.Chats.name()).child(code).get().addOnCompleteListener(task -> {
            if (task.getResult().getValue() == null) {
                callback.onError(new Exception("Group Chat Not Exists!"));
                return;
            }

            if (isExistsInGroup(code)) {
                callback.onError(new Exception("You Had Joined Group!"));
                return;
            }

            List<String> participants = getParticipants(code);
            List<String> chatIds = getChatOfUser();

            database.child(FirebaseKey.Chats.name()).child(code).child("participants").child(participants.size() + "").setValue(userID).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    database.child(FirebaseKey.UserInChat.name()).child(userID).child((chatIds == null ? 0 : chatIds.size()) + "").setValue(code).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            callback.call("Join Success!!!");
                            return;
                        }
                        callback.onError(task2.getException());
                    });
                    return;
                }
                callback.onError(task1.getException());
            });


        });
    }

    public static Boolean sendMessage(String chatId, String message) {
        if (!isUserInChat(chatId)) return false;

        User user = getUserInfo();

        HashMap<String, Object> messageObj = new HashMap<>();

        messageObj.put("message", message);
        messageObj.put("sender", user.Fullname());
        messageObj.put("send_at", new Date().getTime());
        messageObj.put("sender_id", user.userID);
        messageObj.put("type", "text");


        database.child(FirebaseKey.MessagesInChats.name()).child(chatId).getRef().push().setValue(messageObj).addOnCompleteListener(new OnCompleteListener<>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        return true;
    }

    public static void sendImageMessage(String chatId, Uri path) {
        User user = getUserInfo();
        String direction = chatId + "/" + UUID.randomUUID().toString();

        storageReference.child(direction).putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                }


                HashMap<String, Object> messageObj = new HashMap<>();

                messageObj.put("message", "[Hình Ảnh]");
                messageObj.put("sender", user.Fullname());
                messageObj.put("send_at", new Date().getTime());
                messageObj.put("sender_id", user.userID);
                messageObj.put("type", "image");
                messageObj.put("src", direction);

                database.child(FirebaseKey.MessagesInChats.name()).child(chatId).getRef().push().setValue(messageObj).addOnCompleteListener(new OnCompleteListener<>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

            }
        });
    }

    private static Boolean isUserInChat(String chatId) {
        List<String> chats = getChatOfUser();

        for (String chat : chats)
            if (chat.equals(chatId))
                return true;

        return false;
    }

    public static void getChatMessage(String id, Callback callback) {
        database.child(FirebaseKey.Chats.name()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                callback.call(chat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child(FirebaseKey.MessagesInChats.name()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    messages.add(snap.getValue(Message.class));
                }
                callback.call(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getChatRT(String chatId, Callback callback) {
        database.child(FirebaseKey.Chats.name()).child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                callback.call(chat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static Uri getImageUri(String src) {
        Task<Uri> task = storageReference.child(src).getDownloadUrl();

        while (!task.isComplete()) {
        }

        return task.getResult();
    }
    // endregion

    // region Local Function
    private static String makeCode() {
        return randomString(CODE_LENGTH);
    }

    private static String randomString(int length) {
        String str = "";

        String[] characterArray = characters.split("");
        Random rnd = new Random();

        for (int i = 0; i < length; i++) {
            str += characterArray[rnd.nextInt(characters.length())];
        }

        return str;
    }
    // endregion
}
