package com.example.batchcodecapture;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

import com.example.batchcodecapture.data.BarcodeEntry;
import com.example.batchcodecapture.data.DatabaseHelper;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper db;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        db = new DatabaseHelper(context);
        db.getWritableDatabase().execSQL("DROP TABLE IF EXISTS Barcode_storage");
        db.onCreate(db.getWritableDatabase());
        DatabaseHelper.defaultSessionId = 0;
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testAddEntryAndRetrieve() {
        db.addentry("1234567890", "/path/to/image.jpg");

        List<BarcodeEntry> entries = db.getBarcodesForSession("0");
        assertNotNull(entries);
        assertEquals(1, entries.size());

        BarcodeEntry entry = entries.get(0);
        assertEquals("1234567890", entry.getBarcodeData());
        assertEquals("/path/to/image.jpg", entry.getImagePath());
    }

    @Test
    public void testGetAllSessions() {
        db.addentry("barcode1", "/image1");
        db.updateSessionID();
        db.addentry("barcode2", "/image2");

        List<String> sessions = db.getAllSessions();
        assertTrue(sessions.contains("0"));
        assertTrue(sessions.contains("1"));
        assertEquals(2, sessions.size());
    }

    @Test
    public void testUpdateSessionID() {
        int initialSessionId = DatabaseHelper.defaultSessionId;
        db.updateSessionID();
        assertEquals(initialSessionId + 1, DatabaseHelper.defaultSessionId);
    }

    @Test
    public void testGetBarcodesForNonExistentSession() {
        List<BarcodeEntry> entries = db.getBarcodesForSession("999");
        assertNotNull(entries);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testAddMultipleEntriesSameSession() {
        db.addentry("barcode1", "/image1");
        db.addentry("barcode2", "/image2");

        List<BarcodeEntry> entries = db.getBarcodesForSession("0");
        assertEquals(2, entries.size());
    }
}
