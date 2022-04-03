package com.example.memo;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memo.utils.ShowToastMessage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private final ShowToastMessage showToastMessage = new ShowToastMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendRecoveryEmail();
    }

    private void sendRecoveryEmail() {
        TextInputEditText recoveryEmail = findViewById(R.id.recoveryEmail);
        Button sendRecoveryEmailButton = findViewById(R.id.sendPasswordResetEmailButton);

        sendRecoveryEmailButton.setOnClickListener(view -> {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.sendPasswordResetEmail(recoveryEmail.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToastMessage.showToastMessage(getApplicationContext(), "Email has been sent");
                } else {
                    showToastMessage.showToastMessage(getApplicationContext(), "Failed. Something went wrong");
                }
            });
        });
    }
}