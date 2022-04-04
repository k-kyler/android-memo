package com.example.memo;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memo.models.Note;
import com.example.memo.ui.NoteListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView noteList;

    ArrayList<Note> noteItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNoteList();
    }

    private void setNoteList() {
        noteList = findViewById(R.id.noteList);

        NoteListAdapter noteListAdapter = new NoteListAdapter(this, noteItems);
        noteList.setAdapter(noteListAdapter);
        noteList.setLayoutManager(new LinearLayoutManager(this));
    }
}