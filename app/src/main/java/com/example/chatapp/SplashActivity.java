package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.chatapp.Utils.FirebaseUtils;

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

                Log.d("Login", FirebaseUtils.mAuth.getCurrentUser() == null ? "no account" : "have account");

                Intent intent = new Intent(SplashActivity.this, FirebaseUtils.mAuth.getCurrentUser() == null ? SignInActivity.class : MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        LoadDataThread.start();
    }
}
