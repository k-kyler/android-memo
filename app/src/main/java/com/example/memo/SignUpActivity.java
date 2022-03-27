package com.example.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText signUpEmail, signUpPassword, signUpConfirmPassword;
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
        TextView switchToLoginButton = findViewById(R.id.switchToSignInButton);

        switchToLoginButton.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);

            startActivity(intent);
        });
    }

    private void showToastMessage(String message) {
        Context context = getApplicationContext();
        int toastDuration = Toast.LENGTH_SHORT;

        if (!message.matches("")) {
            Toast toast = Toast.makeText(context, message, toastDuration);

            toast.show();
        }
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
                showToastMessage("Please enter your email");
            } else if (signUpPassword.getText().toString().matches("")) {
                showToastMessage("Please enter your password");
            } else if (signUpConfirmPassword.getText().toString().matches("")) {
                showToastMessage("Please enter your confirm password");
            } else if (!signUpPassword.getText().toString().equals(signUpConfirmPassword.getText().toString())) {
                showToastMessage("Your passwords are not match");
            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(), signUpPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, ActiveAccountActivity.class);

                            progressBar.setVisibility(View.GONE);
                            showToastMessage("Sign up successful");
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            showToastMessage("This email have been used");
                        }
                    }
                });
            }
        });
    }
}