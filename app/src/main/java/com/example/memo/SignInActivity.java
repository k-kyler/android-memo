package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memo.utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText signInEmail, signInPassword;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private final ShowToastMessage showToastMessage = new ShowToastMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        switchToSignUp();
        signIn();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfSignedIn();
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
        progressBar = findViewById(R.id.progressBar);

        Button signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            if (signInEmail.getText().toString().matches("")) {
                showToastMessage.showToastMessage(getApplicationContext(), "Please enter your email");
                progressBar.setVisibility(View.GONE);
            } else if (signInPassword.getText().toString().matches("")) {
                showToastMessage.showToastMessage(getApplicationContext(), "Please enter your password");
                progressBar.setVisibility(View.GONE);
            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(signInEmail.getText().toString(), signInPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);

                            progressBar.setVisibility(View.GONE);
                            showToastMessage.showToastMessage(getApplicationContext(), "Sign in successful");
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            showToastMessage.showToastMessage(getApplicationContext(), "Wrong email or password");
                        }
                    }
                });
            }
        });
    }

    private void checkIfSignedIn() { ;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        }
    }
}