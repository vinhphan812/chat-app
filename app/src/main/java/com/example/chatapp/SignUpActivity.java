package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

public class SignUpActivity extends AppCompatActivity {

    Toolbar toolbar;
    NavController navController;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = findViewById(R.id.toolbar2);
        navController = Navigation.findNavController(this, R.id.nav_signin);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fullnameFragment, R.id.addressFragment, R.id.usernamePasswordFragment
        ).build();

        NavigationUI.setupWithNavController(toolbar, navController);
    }
}