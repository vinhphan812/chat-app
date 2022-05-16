package com.example.chatapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MemberAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.ImageAPI;
import com.example.chatapp.Utils.Services;

import java.util.List;

public class InfoChatActivity extends AppCompatActivity {
    Button btnEditGroup, btnOutGroup, btnViewMember, btnDeleteOut;
    ImageView avatar;
    TextView groupName;

    String chatId;
    Chat chat;

    @Override
    protected void onStart() {
        super.onStart();
        chat = Services.getChat(chatId);
        groupName.setText(chat.name);
        ImageAPI.getCircle(String.format("https://avatars.dicebear.com/api/initials/%s.png?size=128", chat.name), avatar);

        btnDeleteOut.setText(chat.created_by.equals(Services.getUserID()) ? "Delete Group" : "Out Group");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_chat);

        chatId = getIntent().getStringExtra("id");

        btnEditGroup = findViewById(R.id.btn_edit_group);
        btnDeleteOut = findViewById(R.id.btn_delete_out);
        btnViewMember = findViewById(R.id.btn_view_member_group);
        avatar = findViewById(R.id.img_avatar_group);



        groupName = findViewById(R.id.tv_name_group);

        btnEditGroup.setOnClickListener(v -> {
            showDialogEditGroup();
        });

        btnViewMember.setOnClickListener(v -> {
            showDialogMemberGroup();
        });

        btnDeleteOut.setOnClickListener(v -> {

        });
    }

    private void showDialogMemberGroup() {
        View view = LayoutInflater.from(InfoChatActivity.this).inflate(R.layout.dialog_member_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfoChatActivity.this);

        RecyclerView rvMember = view.findViewById(R.id.rv_member);

        Services

        MemberAdapter adapter = new MemberAdapter();

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }

    private void showDialogOutGroup() {
        View view = LayoutInflater.from(InfoChatActivity.this).inflate(R.layout.dialog_out_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfoChatActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }

    private void showDialogEditGroup() {
        View view = LayoutInflater.from(InfoChatActivity.this).inflate(R.layout.dialog_join_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfoChatActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }
}