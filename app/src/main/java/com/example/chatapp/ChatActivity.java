package com.example.chatapp;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Models.Chat;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rvMessage;
    ProgressBar progressBar;

    MessageAdapter adapter;
    ImageButton selectImage;

    EditText inputMessage;
    ImageButton btnSend;

    String chatId;
    int RESULT_OK = 1;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, InfoChatActivity.class);

        intent.putExtra("id", chatId);

        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatId");

        toolbar = findViewById(R.id.toolbar);
        rvMessage = findViewById(R.id.rvMessage);
        progressBar = findViewById(R.id.progressBar);
        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        selectImage = findViewById(R.id.select_image);

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

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_OK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Uri chosenImageUri = data.getData();

            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}