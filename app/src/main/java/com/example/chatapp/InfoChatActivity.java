package com.example.chatapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class  InfoChatActivity extends AppCompatActivity {
    Button btnEditGroup,btnOutGroup,btnViewMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_chat);
        btnEditGroup=findViewById(R.id.btn_edit_group);
        btnEditGroup.setOnClickListener(v -> {
            showDialogEditGroup();
        });
        btnOutGroup=findViewById(R.id.btn_out_group);
        btnOutGroup.setOnClickListener(v -> {
            showDialogOutGroup();
        });
        btnViewMember=findViewById(R.id.btn_view_member_group);
        btnViewMember.setOnClickListener(v -> {
            showDialogMemberGroup();
        });
    }

    private void showDialogMemberGroup() {
        View view = LayoutInflater.from(InfoChatActivity.this).inflate(R.layout.dialog_member_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfoChatActivity.this);
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