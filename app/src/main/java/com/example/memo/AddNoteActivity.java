package com.example.memo;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.memo.utils.ShowToastMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView topBarAvatar;
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView pin;
    private Button confirmBtn;
    private boolean isPinned;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final ShowToastMessage showToastMessage = new ShowToastMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        AddNoteInit();
        ToolbarMenu();
        PinNote();
        EditNoteInit();
    }
    private void PinNote() {
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPinned = !isPinned;
                if (isPinned) {
                    setTextViewDrawableColor(pin, R.color.pinned);
                }
                else {
                    setTextViewDrawableColor(pin, R.color.unpin);
                }
            }
        });
    }

    public void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void AddNoteInit() {
        toolbar = findViewById(R.id.topAppBar);

        topBarAvatar = findViewById(R.id.topBarAvatar);
        topBarAvatar.setImageResource(R.drawable.profile);

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);
        confirmBtn = findViewById(R.id.confirmAddNoteButton);

        pin = findViewById(R.id.noteItemPin);
        isPinned = false;

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();

                data.put("title", titleEditText.getText().toString());
                data.put("content", contentEditText.getText().toString());
                data.put("type", "normal");
                data.put("isRemoved", false);
                data.put("isPinned", isPinned);
                data.put("uid", firebaseUser.getUid());
                data.put("createdAt", FieldValue.serverTimestamp());

                db.collection("notes").add(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);

                        showToastMessage.showToastMessage(getApplicationContext(), "Create note successful");
                        startActivity(intent);
                    } else {
                        Log.e("Error query: ", String.valueOf(task.getException()));
                        showToastMessage.showToastMessage(getApplicationContext(), "Failed to create");
                    }
                });
            }
        });
    }

    private void EditNoteInit() {
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            titleEditText.setText(bundle.getString("title", ""));
            contentEditText.setText(bundle.getString("content", ""));
            confirmBtn.setText("Edit Note");
            if(bundle.getBoolean("isPinned")){
                isPinned = true;
                setTextViewDrawableColor(pin, R.color.pinned);
            }
        }

    }

    private void ToolbarMenu() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.openDraw:

                }
                return false;
            }
        });
    }

}