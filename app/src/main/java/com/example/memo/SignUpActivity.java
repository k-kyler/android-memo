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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText signUpUsername, signUpEmail, signUpPassword, signUpConfirmPassword;
    private TextInputLayout signUpUsernameLayout, signUpEmailLayout, signUpPasswordLayout, signUpConfirmPasswordLayout;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private final ShowToastMessage showToastMessage = new ShowToastMessage();

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
        signUpUsername = findViewById(R.id.signUpUsername);
        signUpUsernameLayout = findViewById(R.id.signUpUsernameLayout);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpEmailLayout = findViewById(R.id.signUpEmailLayout);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpPasswordLayout = findViewById(R.id.signUpPasswordLayout);
        signUpConfirmPassword = findViewById(R.id.signUpConfirmPassword);
        signUpConfirmPasswordLayout = findViewById(R.id.signUpConfirmPasswordLayout);
        progressBar = findViewById(R.id.progressBar);

        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            signUpUsernameLayout.setErrorEnabled(false);
            signUpUsernameLayout.setError("");
            signUpEmailLayout.setErrorEnabled(false);
            signUpEmailLayout.setError("");
            signUpPasswordLayout.setErrorEnabled(false);
            signUpPasswordLayout.setError("");
            signUpConfirmPasswordLayout.setErrorEnabled(false);
            signUpConfirmPasswordLayout.setError("");

            if (signUpUsername.getText().toString().matches("")) {
                signUpUsernameLayout.setErrorEnabled(true);
                signUpUsernameLayout.setError("Please enter your username");
                progressBar.setVisibility(View.GONE);
            } else if (signUpEmail.getText().toString().matches("")) {
                signUpEmailLayout.setErrorEnabled(true);
                signUpEmailLayout.setError("Please enter your email");
                progressBar.setVisibility(View.GONE);
            } else if (signUpPassword.getText().toString().matches("")) {
                signUpPasswordLayout.setErrorEnabled(true);
                signUpPasswordLayout.setError("Please enter your password");
                progressBar.setVisibility(View.GONE);
            } else if (signUpConfirmPassword.getText().toString().matches("")) {
                signUpConfirmPasswordLayout.setErrorEnabled(true);
                signUpConfirmPasswordLayout.setError("Please enter your confirm password");
                progressBar.setVisibility(View.GONE);
            } else if (!signUpPassword.getText().toString().equals(signUpConfirmPassword.getText().toString())) {
                signUpConfirmPasswordLayout.setErrorEnabled(true);
                signUpConfirmPasswordLayout.setError("Your passwords are not match");
                progressBar.setVisibility(View.GONE);
            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(), signUpPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest update = new UserProfileChangeRequest
                                    .Builder()
                                    .setDisplayName(signUpUsername.getText().toString())
                                    .build();

                            if (firebaseUser != null) {
                                firebaseUser.updateProfile(update);
                            }

                            Intent intent = new Intent(SignUpActivity.this, ActiveAccountActivity.class);
                            progressBar.setVisibility(View.GONE);
                            showToastMessage.showToastMessage(getApplicationContext(), "Sign up successful");
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            signUpEmailLayout.setErrorEnabled(true);
                            signUpEmailLayout.setError("This email have been used");
                        }
                    }
                });
            }
        });
    }
}