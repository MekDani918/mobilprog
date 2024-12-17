package com.example.notes;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.lang.reflect.Type;


public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder>
{
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note)
    {
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);
        holder.contentTextView.setTextSize(note.fontSize+8);

        if(note.bold && note.italic)
        {
            holder.contentTextView.setTypeface(holder.contentTextView.getTypeface(), Typeface.BOLD_ITALIC);
        }
        else if(note.bold)
        {
            holder.contentTextView.setTypeface(holder.contentTextView.getTypeface(), Typeface.BOLD);
        }
        else if(note.italic)
        {
            holder.contentTextView.setTypeface(holder.contentTextView.getTypeface(), Typeface.ITALIC);
        }
        else
        {
            holder.contentTextView.setTypeface(null, Typeface.NORMAL);
        }

        if(note.underlined)
        {
            holder.contentTextView.setPaintFlags(holder.contentTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        else
        {
            holder.contentTextView.setPaintFlags(holder.contentTextView.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
        if(note.imagePath.equals(""))
        {
            holder.imgThumbnail.setVisibility(View.GONE);
        }
        else
        {
            holder.imgThumbnail.setVisibility(View.VISIBLE);
        }

        holder.contentTextView.setTextColor(Color.parseColor(note.color));

        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));

        holder.itemView.setOnClickListener(v->{
            Intent intent=new Intent(context,NoteDetailsActivity.class);
            intent.putExtra("title",note.title);
            intent.putExtra("content",note.content);
            intent.putExtra("fontSize",note.fontSize);
            intent.putExtra("bold",note.bold);
            intent.putExtra("italic",note.italic);
            intent.putExtra("underlined",note.underlined);
            intent.putExtra("color",note.color);
            intent.putExtra("imagePath",note.imagePath);
            String docId=this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder
    {
        TextView titleTextView, contentTextView, timestampTextView;
        ImageView imgThumbnail;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView=itemView.findViewById(R.id.noteTitleTextView);
            contentTextView=itemView.findViewById(R.id.noteContentTextView);
            timestampTextView=itemView.findViewById(R.id.noteTimestampTextView);
            imgThumbnail=itemView.findViewById(R.id.imgThumbnail);
        }
    }
}
