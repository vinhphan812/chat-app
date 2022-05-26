package com.example.chatapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MemberAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.ImageAPI;
import com.example.chatapp.Utils.Services;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class InfoChatActivity extends AppCompatActivity {
    Button btnEditGroup, btnOutGroup, btnViewMember, btnDeleteOut;
    ImageView avatar;
    TextView groupName, tvCode;
    String chatId;
    MemberAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_chat);

        chatId = getIntent().getStringExtra("id");

        btnEditGroup = findViewById(R.id.btn_edit_group);
        btnDeleteOut = findViewById(R.id.btn_delete_out);
        btnViewMember = findViewById(R.id.btn_view_member_group);
        avatar = findViewById(R.id.img_avatar_group);
        tvCode = findViewById(R.id.tvCode);

        List<User> participants = new ArrayList<>();

        MemberAdapter.Listener listener = user -> {

        };

        adapter = new MemberAdapter(getApplicationContext(), participants, "", listener);

        Services.getChatRT(chatId, new Callback() {
            @Override
            public void call(Chat chat) {
                super.call(chat);
                groupName.setText(chat.name);
                ImageAPI.getDefaultImage(chat.name, avatar);
                btnDeleteOut.setText(chat.created_by.equals(Services.getUserID()) ? "Delete Group" : "Leave Group");
                adapter.AdminId = chat.created_by;
            }
        });


        Services.getParticipantsInChat(chatId, new Callback() {
            @Override
            public void call(Object list) {
                super.call(list);
                List<User> participants = (List<User>) list;
                adapter.list = participants;
                adapter.notifyDataSetChanged();
            }
        });


        groupName = findViewById(R.id.tv_name_group);

        tvCode.setText(chatId);

        tvCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(chatId, chatId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Copy clipboard success", Toast.LENGTH_SHORT).show();
        });

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

        rvMember.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvMember.setAdapter(adapter);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }

    private void showDialogDeleteOut() {
        View view = LayoutInflater.from(InfoChatActivity.this).inflate(R.layout.dialog_out_group, null);
        TextView title = view.findViewById(R.id.title);
        Button btnOut = view.findViewById(R.id.btn_out_group);

        title.setText(adapter.AdminId.equals(Services.getUserID()) ? "Delete Group" : "Leave Group");

        btnOut.setOnClickListener(v -> {
            if (adapter.AdminId.equals(Services.getUserID())) {

            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(InfoChatActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().show();
    }

    private void showDialogEditGroup() {
        View view = LayoutInflater.from(InfoChatActivity.this).inflate(R.layout.dialog_edit_group, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfoChatActivity.this);
        TextInputLayout edtName = view.findViewById(R.id.edt_name_edit_group);
        Button btnChange = view.findViewById(R.id.btn_edit_group);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getEditText().getText().toString();
                Services.ChangeNameGroup(chatId, name, new Callback() {
                    @Override
                    public void call(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }
}