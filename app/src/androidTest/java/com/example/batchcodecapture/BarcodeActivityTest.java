package com.example.batchcodecapture;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.batchcodecapture.db.DatabaseHelper;

@RunWith(AndroidJUnit4.class)
public class BarcodeActivityTest {

    @Mock
    private DatabaseHelper mockDb;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void onCreate_LoadsBarcodesFromDatabase() {
        List<BarcodeEntry> mockEntries = new ArrayList<>();
        mockEntries.add(new BarcodeEntry("test-data", "test-path"));
        when(mockDb.getBarcodesForSession(anyString())).thenReturn(mockEntries);
    }
}