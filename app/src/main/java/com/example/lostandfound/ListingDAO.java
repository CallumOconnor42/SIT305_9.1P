package com.example.lostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListingDAO {
    private SQLiteDatabase database;

    public ListingDAO(Context context) {
        LostAndFoundDatabaseHelper dbHelper = new LostAndFoundDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long addListing(String type, String name, String phoneNumber, String description, String date, String location) {
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("name", name);
        values.put("phone_number", phoneNumber);
        values.put("description", description);
        values.put("date", date);
        values.put("location", location);

        return database.insert("listings", null, values);
    }

    public List<Listing> getAllListings() {
        List<Listing> listings = new ArrayList<>();
        Cursor cursor = database.query("listings", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) { // Check if cursor is not null and contains data
            do {
                Listing listing = new Listing();
                listing.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                listing.setType(cursor.getString(cursor.getColumnIndex("type")));
                listing.setName(cursor.getString(cursor.getColumnIndex("name")));
                listing.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
                listing.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                listing.setDate(cursor.getString(cursor.getColumnIndex("date")));
                listing.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                listings.add(listing);
            } while (cursor.moveToNext());
            cursor.close(); // Close cursor after use
        }
        return listings;
    }

    public boolean removeListing(int id) {
        return database.delete("listings", "_id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Listing getListingById(int id) {
        Listing listing = null;
        Cursor cursor = database.query("listings", null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            listing = new Listing();
            listing.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            listing.setType(cursor.getString(cursor.getColumnIndex("type")));
            listing.setName(cursor.getString(cursor.getColumnIndex("name")));
            listing.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
            listing.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            listing.setDate(cursor.getString(cursor.getColumnIndex("date")));
            listing.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        }
        cursor.close();
        return listing;
    }
}