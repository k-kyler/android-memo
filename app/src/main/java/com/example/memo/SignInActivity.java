package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText signInEmail, signInPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        switchToSignUp();
        signIn();
    }

    private void switchToSignUp() {
        TextView switchToSignUpButton = findViewById(R.id.switchToSignUpButton);

        switchToSignUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);

            startActivity(intent);
        });
    }

    private void signIn() {
        signInEmail = findViewById(R.id.signInEmail);
        signInPassword = findViewById(R.id.signInPassword);

        Button signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(view -> {

        });
    }
}