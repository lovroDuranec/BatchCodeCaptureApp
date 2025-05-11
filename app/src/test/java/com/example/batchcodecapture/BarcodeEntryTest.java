package com.example.batchcodecapture;

import org.junit.Test;
import static org.junit.Assert.*;

public class BarcodeEntryTest {

    @Test
    public void testBarcodeEntryStoresDataCorrectly() {
        BarcodeEntry entry = new BarcodeEntry("ABC123", "/path/to/image.jpg");

        assertEquals("ABC123", entry.getBarcodeData());
        assertEquals("/path/to/image.jpg", entry.getImagePath());
    }

    @Test
    public void testBarcodeEntryWithNulls() {
        BarcodeEntry entry = new BarcodeEntry(null, null);

        assertNull(entry.getBarcodeData());
        assertNull(entry.getImagePath());
    }

    @Test
    public void testEmptyValues() {
        BarcodeEntry entry = new BarcodeEntry("", "");

        assertEquals("", entry.getBarcodeData());
        assertEquals("", entry.getImagePath());
    }

    @Test
    public void testBarcodeDataOnly() {
        BarcodeEntry entry = new BarcodeEntry("1234567890", null);

        assertEquals("1234567890", entry.getBarcodeData());
        assertNull(entry.getImagePath());
    }

    @Test
    public void testImagePathOnly() {
        BarcodeEntry entry = new BarcodeEntry(null, "/path/to/image.jpg");

        assertNull(entry.getBarcodeData());
        assertEquals("/path/to/image.jpg", entry.getImagePath());
    }
}
