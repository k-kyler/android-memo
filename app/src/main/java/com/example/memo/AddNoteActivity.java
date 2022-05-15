package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.memo.ui.CustomDrawing;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView topBarAvatar;
    private EditText titleEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        AddNoteInit();
        ToolbarMenu();

    }

    private void AddNoteInit() {
        toolbar = findViewById(R.id.topAppBar);

        topBarAvatar = findViewById(R.id.topBarAvatar);
        topBarAvatar.setImageResource(R.drawable.android);

        titleEditText = findViewById(R.id.title);
        contentEditText = findViewById(R.id.content);
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