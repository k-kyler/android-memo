package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.memo.ui.CustomDrawing;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView topBarAvatar;
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView pin;
    private boolean isPinned;

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
        topBarAvatar.setImageResource(R.drawable.android);

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);

        pin = findViewById(R.id.noteItemPin);
        isPinned = false;
    }

    private void EditNoteInit() {
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            Log.e(":", "EditNoteInit: " + bundle.getString("noteId"));
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