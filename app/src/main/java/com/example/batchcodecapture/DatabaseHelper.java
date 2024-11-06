package com.example.batchcodecapture;

import android.annotation.SuppressLint;
import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "Storage.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_BARCODE_STORAGE = "Barcode_storage";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SESSION_ID = "session";
    private static final String COLUMN_BARCODE = "barcode";
    private static int defaultSessionId = 1;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME_BARCODE_STORAGE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SESSION_ID + " TEXT, " +
                COLUMN_BARCODE + " TEXT);";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BARCODE_STORAGE);
        onCreate(db);

    }

    void addentry(String barcode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_SESSION_ID, String.valueOf(defaultSessionId));
        long result = db.insert(TABLE_NAME_BARCODE_STORAGE, null, cv);
        if (result   == -1) {
            System.out.println("SQL barcode failed");
        } else {
            System.out.println("SQL barcode success");
        }
    }

    public void updateSessionID(){
        defaultSessionId +=1;
    }

    @SuppressLint("Range")
    public List<String> getAllSessions() {
        List<String> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_SESSION_ID + " FROM " + TABLE_NAME_BARCODE_STORAGE, null);

        if (cursor.moveToFirst()) {
            do {
                sessions.add(cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sessions;
    }

    @SuppressLint("Range")
    public List<String> getBarcodesForSession(String sessionID){
        List<String> barcodes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_BARCODE + " FROM " + TABLE_NAME_BARCODE_STORAGE + " WHERE " + COLUMN_SESSION_ID + " = ?", new String[]{sessionID});


        if (cursor.moveToFirst()) {
            do {
                barcodes.add(cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return barcodes;
    }
 }
