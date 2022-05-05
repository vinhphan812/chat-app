package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.chatapp.Utils.Callback;
import com.example.chatapp.Utils.Services;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread LoadDataThread = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("Login", Services.isLoggedIn() ? "have account" : "no account");

                Services.createGroup("Khải Trần 1", new Callback() {
                    @Override
                    public void onError(Exception error) {
                        super.onError(error);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                Intent intent = new Intent(SplashActivity.this, Services.isLoggedIn() ? MainActivity.class : SignInActivity.class);
                startActivity(intent);
                finish();
            }
        };
        LoadDataThread.start();
    }
}
