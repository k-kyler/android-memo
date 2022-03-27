package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memo.utils.SharedMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText signUpEmail, signUpPassword, signUpConfirmPassword;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private SharedMethods sharedMethods = new SharedMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        switchToSignIn();
        signUp();
    }

    private void switchToSignIn() {
        TextView switchToSignInButton = findViewById(R.id.switchToSignInButton);

        switchToSignInButton.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);

            startActivity(intent);
        });
    }

    private void signUp() {
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpConfirmPassword = findViewById(R.id.signUpConfirmPassword);
        progressBar = findViewById(R.id.progressBar);

        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            if (signUpEmail.getText().toString().matches("")) {
                sharedMethods.showToastMessage(getApplicationContext(), "Please enter your email");
                progressBar.setVisibility(View.GONE);
            } else if (signUpPassword.getText().toString().matches("")) {
                sharedMethods.showToastMessage(getApplicationContext(), "Please enter your password");
                progressBar.setVisibility(View.GONE);
            } else if (signUpConfirmPassword.getText().toString().matches("")) {
                sharedMethods.showToastMessage(getApplicationContext(), "Please enter your confirm password");
                progressBar.setVisibility(View.GONE);
            } else if (!signUpPassword.getText().toString().equals(signUpConfirmPassword.getText().toString())) {
                sharedMethods.showToastMessage(getApplicationContext(), "Your passwords are not match");
                progressBar.setVisibility(View.GONE);
            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(), signUpPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, ActiveAccountActivity.class);

                            progressBar.setVisibility(View.GONE);
                            sharedMethods.showToastMessage(getApplicationContext(), "Sign up successful");
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            sharedMethods.showToastMessage(getApplicationContext(), "This email have been used");
                        }
                    }
                });
            }
        });
    }
}