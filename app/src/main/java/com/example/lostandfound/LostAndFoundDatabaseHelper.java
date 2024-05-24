package com.example.lostandfound;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LostAndFoundDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LostAndFound.db";
    private static final int DATABASE_VERSION = 1;

    public LostAndFoundDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the listings table
        db.execSQL("CREATE TABLE listings (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT," +
                "name TEXT," +
                "phone_number TEXT," +
                "description TEXT," +
                "date TEXT," +
                "location TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the listings table if it exists
        db.execSQL("DROP TABLE IF EXISTS listings");

        // Recreate the table
        onCreate(db);
    }
}