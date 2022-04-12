package com.example.memo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memo.R;
import com.example.memo.models.Note;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {
    Context context;
    List<Note> noteItems;

    public NoteListAdapter(Context context, List<Note> noteItems) {
        this.context = context;
        this.noteItems = noteItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.note_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(noteItems.get(position).getTitle());
        holder.content.setText(noteItems.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return noteItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.noteItemTitle);
            content = itemView.findViewById(R.id.noteItemContent);
        }
    }
}