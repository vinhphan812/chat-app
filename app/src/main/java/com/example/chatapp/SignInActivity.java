package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;
    EditText txtUser, txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        btnSignIn = findViewById(R.id.btn_signIn);
        btnSignUp = findViewById(R.id.btn_signUp);

        txtUser = findViewById(R.id.edt_email);
        txtPass = findViewById(R.id.edt_password);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = txtUser.getText().toString();
                String pass = txtPass.getText().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng điền thông tin đăng nhập", Toast.LENGTH_LONG).show();
                    return;
                }

                btnSignIn.setEnabled(false);

                FirebaseUtils.mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseUtils.mAuth.getCurrentUser();


                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                        }

                        btnSignIn.setEnabled(true);
                    }
                });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}