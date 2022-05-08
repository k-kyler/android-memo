package com.example.memo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memo.models.Note;
import com.example.memo.ui.NoteListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView noteList;
    private FloatingActionButton addNoteButton;
    private NoteListAdapter noteListAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNoteList();
    }

    private ArrayList<Note> getNoteList() {
        ArrayList<Note> noteItems = new ArrayList<>();

        db.collection("notes")
            .orderBy("isPinned", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("uid", firebaseUser.getUid())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = documentSnapshot.getId();
                        String uid = (String) documentSnapshot.getData().get("uid");
                        String title = (String) documentSnapshot.getData().get("title");
                        String type = (String) documentSnapshot.getData().get("type");
                        String content = (String) documentSnapshot.getData().get("content");
                        Timestamp createdAt = (Timestamp) documentSnapshot.getData().get("createdAt");
                        boolean isPinned = (boolean) documentSnapshot.getData().get("isPinned");
                        boolean isRemoved = (boolean) documentSnapshot.getData().get("isRemoved");

                        Note noteItem = new Note(id, uid, title, type, content, createdAt, isPinned, isRemoved);
                        noteItems.add(noteItem);
                    }

                    progressBar = findViewById(R.id.progressBar);

                    progressBar.setVisibility(View.GONE);
                    noteListAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Error query: ", String.valueOf(task.getException()));
                }
            });

        return noteItems;
    }

    private void setNoteList() {
        noteList = findViewById(R.id.noteList);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        noteListAdapter = new NoteListAdapter(this, getNoteList());
        noteList.setAdapter(noteListAdapter);
        noteList.setLayoutManager(new LinearLayoutManager(this));
    }
}