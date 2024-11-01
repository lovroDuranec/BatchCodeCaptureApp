package com.example.batchcodecapture;

import android.content.ContentValues;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "Storage.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_BARCODE_STORAGE = "Barcode_storage";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BARCODE = "barcode";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME_BARCODE_STORAGE +
                "(" + COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
        long result = db.insert(TABLE_NAME_BARCODE_STORAGE, null, cv);
        if (result   == -1) {
            System.out.println("SQL barcode failed");
        } else {
            System.out.println("SQL barcode success");
        }
    }
}
