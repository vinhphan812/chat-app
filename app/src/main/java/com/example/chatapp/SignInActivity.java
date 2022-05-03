package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;
    TextInputLayout txtUser, txtPass;
    CheckBox cbxRememberAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        btnSignIn = findViewById(R.id.btn_signIn);
        btnSignUp = findViewById(R.id.btn_signUp);
        cbxRememberAccount = findViewById(R.id.cbx_remember);
        txtUser = findViewById(R.id.edt_username_signIn);
        txtPass = findViewById(R.id.edt_password_signIn);

        btnSignIn.setOnClickListener(v -> {
            String user = txtUser.getEditText().getText().toString();
            String pass = txtPass.getEditText().getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Snackbar.make(v, "Vui lòng nhập đầy đủ thông tin!", Snackbar.LENGTH_LONG).show();
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
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}