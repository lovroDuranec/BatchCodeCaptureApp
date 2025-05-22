package com.example.batchcodecapture;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import static org.junit.Assert.*;

import com.example.batchcodecapture.db.DatabaseHelper;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.TIRAMISU)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        DatabaseHelper.defaultSessionId = 0;
        dbHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        database = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        database.close();
        dbHelper.close();
    }

    @Test
    public void testDatabaseCreation() {
        Cursor cursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='Barcode_storage'",
                null
        );
        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }

    @Test
    public void testAddEntry() {
        dbHelper.addentry("123456", "/path/to/image.png");
        Cursor cursor = database.rawQuery(
                "SELECT * FROM Barcode_storage WHERE barcode = '123456'",
                null
        );
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals("123456", cursor.getString(cursor.getColumnIndex("barcode")));
        assertEquals("0", cursor.getString(cursor.getColumnIndex("session")));
        cursor.close();
    }

    @Test
    public void testUpdateSessionID() {
        assertEquals(0, DatabaseHelper.defaultSessionId);
        dbHelper.updateSessionID();
        assertEquals(1, DatabaseHelper.defaultSessionId);
        dbHelper.updateSessionID();
        assertEquals(2, DatabaseHelper.defaultSessionId);
    }

    @Test
    public void testGetAllSessions() {
        dbHelper.addentry("111", "/path/1");
        dbHelper.updateSessionID();
        dbHelper.addentry("222", "/path/2");

        List<String> sessions = dbHelper.getAllSessions();
        assertEquals(2, sessions.size());
        assertTrue(sessions.contains("0"));
        assertTrue(sessions.contains("1"));
    }

    @Test
    public void testGetBarcodesForSession() {
        dbHelper.addentry("111", "/path/1");
        dbHelper.addentry("222", "/path/2");
        dbHelper.updateSessionID();
        dbHelper.addentry("333", "/path/3");
        List<BarcodeEntry> entries = dbHelper.getBarcodesForSession("0");
        assertEquals(2, entries.size());
        assertEquals("111", entries.get(0).getBarcodeData());
        assertEquals("/path/1", entries.get(0).getImagePath());
        entries = dbHelper.getBarcodesForSession("1");
        assertEquals(1, entries.size());
        assertEquals("333", entries.get(0).getBarcodeData());
    }

    @Test
    public void testGetBarcodesForInvalidSession() {
        List<BarcodeEntry> entries = dbHelper.getBarcodesForSession("999");
        assertEquals(0, entries.size());
    }

    @Test
    public void testOnUpgrade() {
        dbHelper.onUpgrade(database, 6, 7);
        dbHelper.addentry("test", "/test/path");
        Cursor cursor = database.rawQuery("SELECT * FROM Barcode_storage", null);
        assertEquals(1, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testAddEntryWithNullAndEmptyValues() {
        dbHelper.addentry(null, null);
        dbHelper.addentry("", "");

        List<BarcodeEntry> entries = dbHelper.getBarcodesForSession(String.valueOf(DatabaseHelper.defaultSessionId));
        assertTrue(entries.size() >= 2);

        boolean hasNullEntry = false;
        for (BarcodeEntry entry : entries) {
            if (entry.getBarcodeData() == null || entry.getImagePath() == null ||
                    entry.getBarcodeData().isEmpty() || entry.getImagePath().isEmpty()) {
                hasNullEntry = true;
                break;
            }
        }
        assertTrue(hasNullEntry);
    }

    @Test
    public void testGetBarcodesForNonExistentSession() {
        List<BarcodeEntry> entries = dbHelper.getBarcodesForSession("non_existent_session");
        assertNotNull(entries);
        assertTrue(entries.isEmpty());
    }
}