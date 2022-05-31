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
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView pin;
    private TextView addNoteTitle;
    private Button confirmBtn;
    private boolean isPinned;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final ShowToastMessage showToastMessage = new ShowToastMessage();
    private int numberOfNotes;

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

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);
        confirmBtn = findViewById(R.id.confirmAddNoteButton);

        pin = findViewById(R.id.noteItemPin);
        isPinned = false;

        addNoteTitle = findViewById(R.id.addNoteTitle);

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            if (!bundle.getString("screenTitle", "").equals("Add new note")){
                return;
            }
            addNoteTitle.setText(bundle.getString("screenTitle", ""));
            numberOfNotes = bundle.getInt("numberOfNotes", 0);
        }

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

                Log.e("TAG", "onClick: " + numberOfNotes );
                Log.e("TAG", "onClick: " + firebaseUser.isEmailVerified() );
                if (!firebaseUser.isEmailVerified() && numberOfNotes == 5) {
                    showToastMessage.showToastMessage(getApplicationContext(), "You need to verify your account to create more than 5 notes");
                    return;
                }

                db.collection("notes").add(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);

                        showToastMessage.showToastMessage(getApplicationContext(), "Create note successful");
                        startActivity(intent);
                    } else {
                        Log.e("Error: ", String.valueOf(task.getException()));
                        showToastMessage.showToastMessage(getApplicationContext(), "Failed to create");
                    }
                });
            }
        });
    }

    private void EditNoteInit() {
        addNoteTitle = findViewById(R.id.addNoteTitle);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            if (!bundle.getString("screenTitle", "").equals("Edit note")){
                return;
            }
            titleEditText.setText(bundle.getString("title", ""));
            contentEditText.setText(bundle.getString("content", ""));
            addNoteTitle.setText(bundle.getString("screenTitle", ""));
            if(bundle.getBoolean("isPinned")){
                isPinned = true;
                setTextViewDrawableColor(pin, R.color.pinned);
            }

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("notes")
                            .document(bundle.getString("noteId"))
                            .update(
                                    "title", titleEditText.getText().toString(),
                                    "content", contentEditText.getText().toString(),
                                    "isPinned", isPinned
                            )
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);

                                    showToastMessage.showToastMessage(getApplicationContext(), "Edit note successful");
                                    startActivity(intent);
                                } else {
                                    Log.e("Error: ", String.valueOf(task.getException()));
                                    showToastMessage.showToastMessage(getApplicationContext(), "Failed to edit");
                                }
                    });
                }
            });
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