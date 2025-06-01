package com.example.batchcodecapture;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;

import com.example.batchcodecapture.data.BarcodeEntry;
import com.example.batchcodecapture.data.DatabaseHelper;
@RunWith(AndroidJUnit4.class)
public class BarcodeActivityTest {

    @Mock
    private DatabaseHelper mockDb;
    private ActivityScenario<BarcodeActivity> scenario;
    private Context context;
    private TestApplication testApp;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        testApp = (TestApplication) context.getApplicationContext();
        testApp.setMockDatabaseHelper(mockDb); // Inject mock here
    }

    private void launchActivity() {
        Intent intent = new Intent(context, BarcodeActivity.class);
        intent.putExtra("SESSION_ID", "0");
        scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void onCreate_LoadsBarcodesFromDatabase() {
        List<BarcodeEntry> mockEntries = new ArrayList<>();
        mockEntries.add(new BarcodeEntry("https://example.com", "/fake/path/image.jpg"));
        when(mockDb.getBarcodesForSession("0")).thenReturn(mockEntries);

        launchActivity();

        scenario.onActivity(activity -> {
            verify(mockDb).getBarcodesForSession("0");

            List<BarcodeEntry> entries = activity.getBarcodeEntries();
            assertEquals(1, entries.size());
            assertEquals("https://example.com", entries.get(0).getBarcodeData());
            assertEquals("/fake/path/image.jpg", entries.get(0).getImagePath());
        });
    }
}