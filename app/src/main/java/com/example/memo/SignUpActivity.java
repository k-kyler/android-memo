package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText signUpEmail, signUpPassword, signUpConfirmPassword;
    private Button signUpButton;
    private TextView switchToLoginButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        switchToLogin();
        signUp();
    }

    private void switchToLogin() {
        switchToLoginButton = findViewById(R.id.switchToSignInButton);
        switchToLoginButton.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    private void signUp() {
        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(view -> {

        });
    }
}