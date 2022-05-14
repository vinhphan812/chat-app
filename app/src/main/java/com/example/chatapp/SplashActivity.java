package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

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

//                Services.createGroup("Vinh Phan 1", new Callback() {
//                    @Override
//                    public void onError(Exception error) {
//                        super.onError(error);
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });

//                Services.sendMessage("cHs4X3gBh2", "Hiii");

                Intent intent = new Intent(SplashActivity.this, Services.isLoggedIn() ? MainActivity.class : SignInActivity.class);
                startActivity(intent);
                finish();
            }
        };
        LoadDataThread.start();
    }
}
