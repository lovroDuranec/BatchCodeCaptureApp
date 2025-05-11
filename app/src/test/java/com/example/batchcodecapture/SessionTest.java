package com.example.batchcodecapture;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void testSessionStoresDataCorrectly() {
        List<String> barcodes = Arrays.asList("123456", "789012");
        Session session = new Session("session1", barcodes);

        assertEquals("session1", session.getSessionId());
        assertEquals(2, session.getBarcodes().size());
        assertTrue(session.getBarcodes().contains("123456"));
    }

    @Test
    public void testEmptyBarcodeList() {
        Session session = new Session("emptySession", Collections.emptyList());

        assertEquals("emptySession", session.getSessionId());
        assertTrue(session.getBarcodes().isEmpty());
    }

    @Test
    public void testNullBarcodeList() {
        Session session = new Session("nullSession", null);

        assertEquals("nullSession", session.getSessionId());
        assertNull(session.getBarcodes());
    }

    @Test
    public void testMultipleSessions() {
        List<String> barcodes1 = Arrays.asList("12345", "67890");
        List<String> barcodes2 = Arrays.asList("54321", "09876");
        Session session1 = new Session("session123", barcodes1);
        Session session2 = new Session("session456", barcodes2);

        assertNotEquals(session1.getSessionId(), session2.getSessionId());

        assertNotEquals(session1.getBarcodes(), session2.getBarcodes());
    }
}
