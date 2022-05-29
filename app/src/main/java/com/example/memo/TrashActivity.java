package com.example.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memo.models.Note;
import com.example.memo.ui.NoteListAdapter;
import com.example.memo.ui.RecyclerItemClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity {
    private ArrayList<Note> noteArrayList;
    private ProgressBar progressBar;
    private RecyclerView noteList;
    private Toolbar toolbar;
    private NoteListAdapter noteListAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        setNoteList();
        ToolbarMenu();
        NoteItemsClickHandler();
    }

    private void NoteItemsClickHandler() {
        builder = new AlertDialog.Builder(this);
        noteList.addOnItemTouchListener(new RecyclerItemClickListener(this, noteList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                builder.setMessage("Completely remove " + noteArrayList.get(position).getTitle() + "?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle completely remove note from firestore here
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Warning!");
                alertDialog.show();
            }
        }));
    }

    private void emptyTrash() {
        // Handle completely remove all notes here
    }

    private void ToolbarMenu() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.emptyTrash:
                        emptyTrash();
                        break;
                }
                return false;
            }
        });
    }

    private ArrayList<Note> getNoteList() {
        ArrayList<Note> noteItems = new ArrayList<>();

        db.collection("notes")
                .orderBy("isPinned", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereEqualTo("uid", firebaseUser.getUid())
                .whereEqualTo("isRemoved", true)
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
        toolbar = findViewById(R.id.topAppBar);

        progressBar.setVisibility(View.VISIBLE);
        noteArrayList = getNoteList();
        noteListAdapter = new NoteListAdapter(this, noteArrayList);
        noteList.setAdapter(noteListAdapter);
        noteList.setLayoutManager(new LinearLayoutManager(this));
    }
}