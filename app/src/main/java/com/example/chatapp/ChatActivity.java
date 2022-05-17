package com.example.chatapp;

import static java.lang.Thread.sleep;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    ImageButton selectImage, btnSend, btnRmImage;

    EditText inputMessage;
    ImageView uploadImage;
    ConstraintLayout uploadLayout;

    String chatId;
    int GALARY_REQUEST = 2;
    Uri chosenImageUri;

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
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

        if(item.getItemId() == R.id.menu_info_chat){
            intent.putExtra("id", chatId);

            startActivity(intent);
        }

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
        uploadImage = findViewById(R.id.imageUpload);
        uploadLayout = findViewById(R.id.uploadLayout);
        btnRmImage = findViewById(R.id.removeImage);


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<Message> messages = new ArrayList<>();

        MessageAdapter.LongListener longListener = message -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(message.message, message.message);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Copy clipboard success", Toast.LENGTH_SHORT).show();
        };

        adapter = new MessageAdapter(this, messages, longListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(layoutManager);
        rvMessage.setHasFixedSize(true);
        rvMessage.setItemViewCacheSize(20);


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

        btnRmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImageUri = null;
                uploadLayout.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMessage.getText().toString();

                if(chosenImageUri != null) {
                    Services.sendImageMessage(chatId, chosenImageUri);
                    chosenImageUri = null;
                    uploadLayout.setVisibility(View.GONE);
                    inputMessage.setText("");
                    inputMessage.clearFocus();
                    return;
                }

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
                startActivityForResult(photoPickerIntent, GALARY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALARY_REQUEST && resultCode == RESULT_OK)
        {
            chosenImageUri = data.getData();

            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);

                uploadImage.setImageBitmap(mBitmap);
                uploadLayout.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                Log.d("Load Image Error", e.getMessage());
            }
        }
    }
}