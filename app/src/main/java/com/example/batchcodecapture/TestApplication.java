package com.example.batchcodecapture;

import android.app.Application;

import com.example.batchcodecapture.data.DatabaseHelper;

public class TestApplication extends Application {
    private DatabaseHelper mockDatabaseHelper;

    public void setMockDatabaseHelper(DatabaseHelper mock) {
        this.mockDatabaseHelper = mock;
    }

    public DatabaseHelper getDatabaseHelper() {
        return mockDatabaseHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mockDatabaseHelper == null) {
            mockDatabaseHelper = new DatabaseHelper(this);
        }
    }
}