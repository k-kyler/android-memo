package com.example.memo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActiveAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_account);

        skipToMain();
        sendVerificationEmail();
    }

    @Override
    protected void onStart() {
        super.onStart();

        reloadUser();
    }

    private void sendVerificationEmail() {
        Button sendEmailButton = findViewById(R.id.sendEmail);

        sendEmailButton.setOnClickListener(view -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {
                firebaseUser.sendEmailVerification();
                sendEmailButton.setEnabled(false);
                sendEmailButton.setText("Sent");
            }
        });
    }

    private void skipToMain() {
        TextView skipActivationButton = findViewById(R.id.skipActivation);

        skipActivationButton.setOnClickListener(view -> {
            Intent intent = new Intent(ActiveAccountActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        });
    }

    private void reloadUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.reload();
        }
    }
}