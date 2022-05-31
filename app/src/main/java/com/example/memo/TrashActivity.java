package com.example.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memo.models.Note;
import com.example.memo.ui.NoteListAdapter;
import com.example.memo.ui.RecyclerItemClickListener;
import com.example.memo.utils.ShowToastMessage;
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
    private final ShowToastMessage showToastMessage = new ShowToastMessage();
    private TextView trashTitle;
    private int numberOfNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        trashTitle = findViewById(R.id.trashTitle);
        trashTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrashActivity.this, MainActivity.class));
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            numberOfNotes = bundle.getInt("numberOfNotes", 0);
        }
        setNoteList();
        ToolbarMenu();
        NoteItemsClickHandler();
    }

    private void NoteItemsClickHandler() {
        builder = new AlertDialog.Builder(this);
        noteList.addOnItemTouchListener(new RecyclerItemClickListener(this, noteList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                builder.setMessage("Completely remove " + noteArrayList.get(position).getTitle() + "?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("notes")
                                .document(noteArrayList.get(position).getId())
                                .delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        noteArrayList.remove(noteArrayList.get(position));
                                        noteListAdapter.notifyDataSetChanged();
                                        showToastMessage.showToastMessage(getApplicationContext(), "Remove note successful");
                                    } else {
                                        Log.e("Error: ", String.valueOf(task.getException()));
                                        showToastMessage.showToastMessage(getApplicationContext(), "Failed to remove note");
                                    }
                                });
                    }
                }).setNegativeButton("Restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!firebaseUser.isEmailVerified() && numberOfNotes + 1 > 5) {
                            showToastMessage.showToastMessage(getApplicationContext(), "You need to verify your account to add more than 5 notes");
                            return;
                        }
                        db.collection("notes")
                                .document(noteArrayList.get(position).getId())
                                .update("isRemoved", false)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        noteArrayList.remove(noteArrayList.get(position));
                                        noteListAdapter.notifyDataSetChanged();
                                        showToastMessage.showToastMessage(getApplicationContext(), "Restore note successful");
                                    } else {
                                        Log.e("Error: ", String.valueOf(task.getException()));
                                        showToastMessage.showToastMessage(getApplicationContext(), "Failed to restore note");
                                    }
                                });
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Hey there!");
                alertDialog.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void restoreAll() {
        if (!firebaseUser.isEmailVerified() && numberOfNotes + noteArrayList.size() > 5) {
            showToastMessage.showToastMessage(getApplicationContext(), "You need to verify your account to add more than 5 notes");
            return;
        }
        for (int i = 0; i < noteArrayList.size(); i++) {
            int position = i;
            db.collection("notes")
                    .document(noteArrayList.get(position).getId())
                    .update("isRemoved", false);
        }
        noteArrayList.clear();
        noteListAdapter.notifyDataSetChanged();
    }

    private void emptyTrash() {
        for (int i = 0; i < noteArrayList.size(); i++) {
            int position = i;
            db.collection("notes")
                    .document(noteArrayList.get(position).getId())
                    .delete();
        }
        noteArrayList.clear();
        noteListAdapter.notifyDataSetChanged();
    }

    private void ToolbarMenu() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.restoreAll:
                        restoreAll();
                        break;

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