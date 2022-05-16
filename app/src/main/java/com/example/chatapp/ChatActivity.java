package com.example.chatapp;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.Services;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rvMessage;
    ProgressBar progressBar;

    MessageAdapter adapter;

    EditText inputMessage;
    ImageButton btnSend;
    Menu mMenu;
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_info, menu);
//        mMenu = menu;
//        View actionView =(View) mMenu.findItem(R.id.menu_info_chat).getActionView();
//        actionView.setOnClickListener(v -> {
//
//        });
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String chatId = getIntent().getStringExtra("chatId");

        toolbar = findViewById(R.id.toolbar);
        rvMessage = findViewById(R.id.rvMessage);
        progressBar = findViewById(R.id.progressBar);
        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<Message> messages = new ArrayList<>();

        adapter = new MessageAdapter(this, messages);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(layoutManager);

        rvMessage.setAdapter(adapter);

        Services.getChatMessage(chatId, new Callback() {
            @Override
            public void call(Object list) {
                List<Message> messages = (List<Message>) list;
                adapter.list = messages;
                adapter.notifyDataSetChanged();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rvMessage.scrollToPosition(adapter.list.size() + 1);
            }

            @Override
            public void call(Chat chat) {
                toolbar.setTitle(chat.name);
                progressBar.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMessage.getText().toString();

                if (message.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your message!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Services.sendMessage(chatId, message);

                inputMessage.setText("");
                inputMessage.clearFocus();
            }
        });
    }
}