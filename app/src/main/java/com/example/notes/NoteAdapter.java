package com.example.notes;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder>
{
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.noteTitleTextView.setText(note.title);
        holder.noteContentTextView.setText(note.content);
        holder.noteContentTextView.setTextSize(note.fontSize+8);

        if(note.bold && note.italic)
        {
            holder.noteContentTextView.setTypeface(holder.noteContentTextView.getTypeface(), Typeface.BOLD_ITALIC);
        }
        else if(note.bold)
        {
            holder.noteContentTextView.setTypeface(holder.noteContentTextView.getTypeface(), Typeface.BOLD);
        }
        else if(note.italic)
        {
            holder.noteContentTextView.setTypeface(holder.noteContentTextView.getTypeface(), Typeface.ITALIC);
        }
        else
        {
            holder.noteContentTextView.setTypeface(null, Typeface.NORMAL);
        }

        if(note.underlined)
        {
            holder.noteContentTextView.setPaintFlags(holder.noteContentTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        else
        {
            holder.noteContentTextView.setPaintFlags(holder.noteContentTextView.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
        if(note.imagePath.equals(""))
        {
            holder.imgThumbnail.setVisibility(View.GONE);
        }
        else
        {
            holder.imgThumbnail.setVisibility(View.VISIBLE);
        }

        holder.noteContentTextView.setTextColor(Color.parseColor(note.color));

        holder.noteTimestampTextView.setText(Helper.timestampToString(note.timestamp));

        holder.itemView.setOnClickListener(v->{
            Intent intent=new Intent(context,NoteDetailsActivity.class);
            intent.putExtra("title", note.title);
            intent.putExtra("content", note.content);
            intent.putExtra("fontSize", note.fontSize);
            intent.putExtra("bold", note.bold);
            intent.putExtra("italic", note.italic);
            intent.putExtra("underlined", note.underlined);
            intent.putExtra("color", note.color);
            intent.putExtra("imagePath", note.imagePath);
            String docId=this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitleTextView, noteContentTextView, noteTimestampTextView;
        ImageView imgThumbnail;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitleTextView = itemView.findViewById(R.id.noteTitleTextView);
            noteContentTextView = itemView.findViewById(R.id.noteContentTextView);
            noteTimestampTextView = itemView.findViewById(R.id.noteTimestampTextView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}
