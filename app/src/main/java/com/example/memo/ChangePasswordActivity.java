package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memo.utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText currentPassword, newPassword, newConfirmPassword;
    private TextInputLayout currentPasswordLayout, newPasswordLayout, newConfirmPasswordLayout;
    private final ShowToastMessage showToastMessage = new ShowToastMessage();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.currentPassword);
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        newPassword = findViewById(R.id.newPassword);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        newConfirmPassword = findViewById(R.id.newConfirmPassword);
        newConfirmPasswordLayout = findViewById(R.id.newConfirmPasswordLayout);
        progressBar = findViewById(R.id.progressBar);

        Button confirmButton = findViewById(R.id.changePassButton);

        confirmButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            currentPasswordLayout.setErrorEnabled(false);
            currentPasswordLayout.setError("");
            newPasswordLayout.setErrorEnabled(false);
            newPasswordLayout.setError("");
            newConfirmPasswordLayout.setErrorEnabled(false);
            newConfirmPasswordLayout.setError("");

            if (currentPassword.getText().toString().matches("")) {
                currentPasswordLayout.setErrorEnabled(true);
                currentPasswordLayout.setError("Please enter current password");
                progressBar.setVisibility(View.GONE);
            } else if (newPassword.getText().toString().matches("")) {
                newPasswordLayout.setErrorEnabled(true);
                newPasswordLayout.setError("Please enter new password");
                progressBar.setVisibility(View.GONE);
            } else if (newConfirmPassword.getText().toString().matches("")) {
                newConfirmPasswordLayout.setErrorEnabled(true);
                newConfirmPasswordLayout.setError("Please enter confirm password");
                progressBar.setVisibility(View.GONE);
            } else if (!newConfirmPassword.getText().toString().matches(newPassword.getText().toString())) {
                newPasswordLayout.setErrorEnabled(true);
                newConfirmPasswordLayout.setErrorEnabled(true);
                newPasswordLayout.setError("Passwords do not match");
                newConfirmPasswordLayout.setError("Passwords do not match");
                progressBar.setVisibility(View.GONE);
            } else {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail().toString(), currentPassword.getText().toString());
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser
                                        .updatePassword(newPassword.getText().toString())
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                                showToastMessage.showToastMessage(getApplicationContext(), "Change password successful");
                                                startActivity(intent);
                                            } else {
                                                showToastMessage.showToastMessage(getApplicationContext(),"Password must equal to or greater than 6 characters");
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                showToastMessage.showToastMessage(getApplicationContext(), "Wrong current password");
                            }
                        }
                    });
                }
            }
        });
    }
}