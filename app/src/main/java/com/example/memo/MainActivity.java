package com.example.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memo.models.Note;
import com.example.memo.ui.NoteListAdapter;
import com.example.memo.ui.RecyclerItemClickListener;
import com.example.memo.utils.ShowToastMessage;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Note> noteArrayList;
    private ProgressBar progressBar;
    private RecyclerView noteList;
    private FloatingActionButton addNoteButton;
    private Toolbar toolbar;
    private NoteListAdapter noteListAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean isGrid = false;
    private SearchView searchView;
    private TextView notFoundText;
    private TextView userName;
    private TextView userEmail;
    private ImageView topBarAvatar;
    private AlertDialog.Builder builder;
    private final ShowToastMessage showToastMessage = new ShowToastMessage();
    private DrawerLayout drawerLayout;
    private NavigationView headerDrawer;
    private Chip userVerifiedStatus;
    private Chip userUnverifiedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNoteList();
        ToolbarMenu();
        AddNote();
        NoteItemsClickHandler();
        openDrawer();
    }

    private void NoteItemsClickHandler() {
        builder = new AlertDialog.Builder(this);
        noteList.addOnItemTouchListener(new RecyclerItemClickListener(this, noteList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("noteId", noteArrayList.get(position).getId());
                bundle.putString("title", noteArrayList.get(position).getTitle());
                bundle.putString("type", noteArrayList.get(position).getType());
                bundle.putString("content", noteArrayList.get(position).getContent());
                bundle.putBoolean("isPinned", noteArrayList.get(position).isPinned());
                bundle.putString("screenTitle", "Edit note");
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                builder.setMessage("Remove " + noteArrayList.get(position).getTitle() + "?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("notes")
                                .document(noteArrayList.get(position).getId())
                                .update("isRemoved", true)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
//                                        noteArrayList.remove(noteArrayList.get(position));
//                                        noteListAdapter.notifyDataSetChanged();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                                        finish();
                                        showToastMessage.showToastMessage(getApplicationContext(), "Have moved note to trash");
                                    } else {
                                        Log.e("Error: ", String.valueOf(task.getException()));
                                        showToastMessage.showToastMessage(getApplicationContext(), "Failed to move note to trash");
                                    }
                                });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Hey there!");
                alertDialog.show();
            }
        }));
    }

    private void ToolbarMenu() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FilterText(newText);
                return false;
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.changeLayout:
                        ChangeLayout();
                        break;
                }
                return false;
            }
        });
    }

    private void FilterText(String newText) {
        ArrayList<Note> filteredlist = new ArrayList<>();
        filteredlist.clear();
        notFoundText.setVisibility(View.GONE);
        for (Note item : noteArrayList) {
            if (item.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                filteredlist.add(item);
            } else if (item.getContent().toLowerCase().contains(newText.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            notFoundText.setVisibility(View.VISIBLE);
        }
        noteListAdapter.filterList(filteredlist);
    }

    private void ChangeLayout() {
        isGrid = !isGrid;
        if (isGrid){
            noteList.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else {
            noteList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void AddNote(){
        addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!firebaseUser.isEmailVerified() && noteArrayList.size() == 5) {
                    showToastMessage.showToastMessage(getApplicationContext(), "You need to verify your account to create more than 5 notes");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("screenTitle", "Add new note");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private ArrayList<Note> getNoteList() {
        ArrayList<Note> noteItems = new ArrayList<>();

        db.collection("notes")
            .orderBy("isPinned", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("uid", firebaseUser.getUid())
            .whereEqualTo("isRemoved", false)
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

        topBarAvatar = findViewById(R.id.topBarAvatar);

        //if (firebaseUser != null && firebaseUser.getPhotoUrl().toString() != null) {
            //Glide.with(getApplicationContext()).load(firebaseUser.getPhotoUrl()).centerCrop().into(topBarAvatar);
        //} else {
            topBarAvatar.setImageResource(R.drawable.profile);
        //}

        searchView = findViewById(R.id.search);
        //searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>Search</font>"));
        notFoundText = findViewById(R.id.notFoundText);

        progressBar.setVisibility(View.VISIBLE);
        noteArrayList = getNoteList();
        noteListAdapter = new NoteListAdapter(this, noteArrayList);
        noteList.setAdapter(noteListAdapter);

        if (isGrid){
            noteList.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else {
            noteList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void openDrawer() {
        topBarAvatar = findViewById(R.id.topBarAvatar);
        drawerLayout = findViewById(R.id.drawerLayout);
        headerDrawer = findViewById(R.id.headerDrawer);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        topBarAvatar.setOnClickListener(view -> {
            userVerifiedStatus = findViewById(R.id.userVerifiedStatus);
            userUnverifiedStatus = findViewById(R.id.userUnverifiedStatus);
            userName = findViewById(R.id.userName);
            userEmail = findViewById(R.id.userEmail);

            if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                userVerifiedStatus.setVisibility(View.VISIBLE);
            } else {
                userUnverifiedStatus.setVisibility(View.VISIBLE);
                userUnverifiedStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, ActiveAccountActivity.class));
                        finish();
                    }
                });
            }

            userName.setText(firebaseUser.getDisplayName());
            userEmail.setText(firebaseUser.getEmail());
            drawerLayout.open();
        });
        headerDrawer.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.myNotes:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    break;

                case R.id.setting:
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    break;

                case R.id.trash:
                    Intent intent = new Intent(MainActivity.this, TrashActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("numberOfNotes", noteArrayList.size());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;

                case R.id.signOut:
                    if(firebaseUser != null){
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                    }
                    break;
            }
            drawerLayout.close();
            return false;
        });
    }
}