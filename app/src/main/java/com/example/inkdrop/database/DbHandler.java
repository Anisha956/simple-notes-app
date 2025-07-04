package com.example.inkdrop.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.inkdrop.Note;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {

    private static  final int DATABASE_VERSION = 1;
    private static  final String DATABASE_NAME = "notes_db";
    private static  final String NOTES_TABLE = "notes";

    public DbHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "timestamp INTEGER )";

        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
      onCreate(db);
    }

    public void addNotesHelper(String title1, String content1, long timestamp) {

        ContentValues values = new ContentValues();
        values.put("title", title1);
        values.put("content", content1);
        values.put("timestamp",timestamp);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(NOTES_TABLE,null,values);
    }
    @SuppressLint("Range")
    public List<Note> getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> noteList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NOTES_TABLE  ,null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex("id")));
                note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                note.setContent(cursor.getString(cursor.getColumnIndex("content")));
                note.setTimestamp(cursor.getLong(cursor.getColumnIndex("timestamp")));

                noteList.add(note);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return noteList;
    }

    public void deleteNotes(String title1) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES_TABLE,"title=?",new String[]{title1});
        db.close();
    }
    public void updateNotesHelper(int id,String newTitle , String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("content", newContent);
        db.update(NOTES_TABLE,values,"id=?",new String[]{String.valueOf(id)});
        db.close();
    }

}

