package com.example.inkdrop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inkdrop.adapter.MyAdapter;
import com.example.inkdrop.database.DbHandler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab_addNote;
    DbHandler db;
    RecyclerView recycler_view;
    MyAdapter adapter;
    List<Note> noteList;
    EditText edit_text_title , edit_text_content;
    FloatingActionButton fab_saveNote;
    SearchView search_view;

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fab_addNote = findViewById(R.id.fab_addNote);
        search_view = findViewById(R.id.search_view);
        recycler_view = findViewById(R.id.recycler_view);


        // Add notes
        fab_addNote.setOnClickListener((v -> showBottomSheet()));

        // Search bar
        search_view.setQueryHint("Search notes....");
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

//  ---------- * Setting up RecyclerView and adapter * ----------

        db = new DbHandler(this);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        noteList = db.getAllNotes();
        Collections.reverse(noteList);

        adapter = new MyAdapter(db, noteList, this);
        recycler_view.setAdapter(adapter);

    }
// ---------- * Refresh notes and update UI * ----------
        @SuppressLint("NotifyDataSetChanged")
        private void refreshNote() {
            List<Note> newList = db.getAllNotes();
            Collections.reverse(newList);
            noteList.clear();
            noteList.addAll(newList);

            adapter.setNotes(noteList);
        }

//  ---------- * for adding a note * -------------------------------
    @SuppressLint("InflateParams")
    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_add_new_notes,null);

        edit_text_title = view.findViewById(R.id.edit_text_title);
        edit_text_content = view.findViewById(R.id.edit_text_content);
        fab_saveNote = view.findViewById(R.id.fab_saveNote);

        fab_saveNote.setOnClickListener(v -> {
            String title1 = edit_text_title.getText().toString().trim();
            String content1 = edit_text_content.getText().toString().trim();

            if (title1.isEmpty() ) {
                edit_text_title.setError("Title is required");
                return;
            }
            if (content1.isEmpty()){
                edit_text_content.setError("Content is required");
                return;
            }

            long currentTime = System.currentTimeMillis();
            db.addNotesHelper(title1,content1,currentTime);

            //update ui
            refreshNote();

            Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.setContentView(view);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.show();

    }

//   ---------- * for editing an existing note * -------------------------------
    @SuppressLint("InflateParams")
    public void showEditBottomSheet(List<Note> noteList, int position , MyAdapter adapter) {
        Note editNote = noteList.get(position);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_add_new_notes,null);

        edit_text_title = view.findViewById(R.id.edit_text_title);
        edit_text_content = view.findViewById(R.id.edit_text_content);
        fab_saveNote = view.findViewById(R.id.fab_saveNote);

        edit_text_title.setText(editNote.getTitle());
        edit_text_content.setText(editNote.getContent());

        fab_saveNote.setOnClickListener(v -> {
            String updatedTitle = edit_text_title.getText().toString().trim();
            String updatedContent = edit_text_content.getText().toString().trim();


            if (updatedTitle.isEmpty() ) {
                edit_text_title.setError("Title is required");
                return;
            }
            if (updatedContent.isEmpty()){
                edit_text_content.setError("Content is required");
                return;
            }

            db.updateNotesHelper(editNote.getId(),updatedTitle , updatedContent);
            refreshNote();


            Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.setContentView(view);
        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        bottomSheetDialog.show();

    }

}