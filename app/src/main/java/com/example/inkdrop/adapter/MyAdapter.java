package com.example.inkdrop.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inkdrop.MainActivity;
import com.example.inkdrop.Note;
import com.example.inkdrop.R;
import com.example.inkdrop.database.DbHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {

    DbHandler db;
    List<Note> noteList;
    List<Note> fullList;
    Context context;
    public String searchText = "";


    public MyAdapter(DbHandler db, List<Note> noteList, Context context) {
        this.db = db;
        this.noteList = noteList;
        this.context = context;

        this.fullList = new ArrayList<>(noteList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Note note = noteList.get(position);

        highlightText(holder.text_view_title, note.getTitle());
        highlightText(holder.text_view_content,note.getContent());

        long timestamp = note.getTimestamp(); // Use stored timestamp

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(new Date(timestamp));
        holder.text_view_time.setText(formattedTime);

//  ------------- * Logic to edit a note * --------

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showEditBottomSheet(noteList, position, MyAdapter.this);
            }
        });

//  ------------- * Note deletion logic * -----------------

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete note")
                    .setMessage("Are you sure, you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int position1 = holder.getAdapterPosition();
                        String noteTitle = noteList.get(position1).getTitle();
                        db.deleteNotes(noteTitle);
                        Toast.makeText(context, "Note deleted!", Toast.LENGTH_SHORT).show();
                        noteList.remove(position1);
                        notifyItemRemoved(position1);
                    })
                    .setNegativeButton("No",null)
                    .show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNotes(List<Note> newNotes) {
        this.noteList = newNotes;
        this.fullList = new ArrayList<>(newNotes);
        notifyDataSetChanged();
    }

//  ------------- * Logic for the SearchBar * --------

    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private final Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note note : fullList) {
                    if (note.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            noteList.clear();
            noteList.addAll((List) results.values);
            searchText = constraint != null ? constraint.toString() : "";
            notifyDataSetChanged();
        }
    };


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_view_title , text_view_content , text_view_time;
      public ViewHolder(@NonNull View itemView) {
          super(itemView);

          text_view_title = itemView.findViewById(R.id.text_view_title);
          text_view_content = itemView.findViewById(R.id.text_view_content);
          text_view_time = itemView.findViewById(R.id.text_view_time);

      }
   }

// --------- * Logic to highlight the entered text in the SearchBar * ---------

    private void highlightText(TextView textView, String originalText) {
        if (searchText == null || searchText.isEmpty()) {
            textView.setText(originalText);
            return;
        }

        String lowerText = originalText.toLowerCase();
        String lowerSearch = searchText.toLowerCase();
        int startIndex = lowerText.indexOf(lowerSearch);

        if (startIndex == -1) {
            textView.setText(originalText);
            return;
        }

        SpannableString spannable = new SpannableString(originalText);
        spannable.setSpan(
                new ForegroundColorSpan(Color.BLUE), // or any highlight color
                startIndex,
                startIndex + searchText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                startIndex,
                startIndex + searchText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textView.setText(spannable);
    }

}

