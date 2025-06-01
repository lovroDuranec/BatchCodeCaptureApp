package com.example.batchcodecapture;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.batchcodecapture.data.BarcodeEntry;

public class BarcodeEntryTest {

    @Test
    public void testBarcodeEntryStoresDataCorrectly() {
        BarcodeEntry entry = new BarcodeEntry("ABC123", "/path/to/image.jpg", "2025-06-01 10:30:00");

        assertEquals("ABC123", entry.getBarcodeData());
        assertEquals("/path/to/image.jpg", entry.getImagePath());
        assertEquals("2025-06-01 10:30:00", entry.getTimestamp());
    }

    @Test
    public void testBarcodeEntryWithNulls() {
        BarcodeEntry entry = new BarcodeEntry(null, null, null);

        assertNull(entry.getBarcodeData());
        assertNull(entry.getImagePath());
        assertNull(entry.getTimestamp());
    }

    @Test
    public void testEmptyValues() {
        BarcodeEntry entry = new BarcodeEntry("", "", "");

        assertEquals("", entry.getBarcodeData());
        assertEquals("", entry.getImagePath());
        assertEquals("", entry.getTimestamp());
    }

    @Test
    public void testBarcodeDataOnly() {
        BarcodeEntry entry = new BarcodeEntry("1234567890", null, null);

        assertEquals("1234567890", entry.getBarcodeData());
        assertNull(entry.getImagePath());
        assertNull(entry.getTimestamp());
    }

    @Test
    public void testImagePathOnly() {
        BarcodeEntry entry = new BarcodeEntry(null, "/path/to/image.jpg", null);

        assertNull(entry.getBarcodeData());
        assertEquals("/path/to/image.jpg", entry.getImagePath());
        assertNull(entry.getTimestamp());
    }

    @Test
    public void testTimestampOnly() {
        BarcodeEntry entry = new BarcodeEntry(null, null, "2025-06-01 11:00:00");

        assertNull(entry.getBarcodeData());
        assertNull(entry.getImagePath());
        assertEquals("2025-06-01 11:00:00", entry.getTimestamp());
    }
}
