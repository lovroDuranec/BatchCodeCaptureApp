// SessionActivityTest.java
package com.example.batchcodecapture;

import android.content.Intent;
import android.widget.ListView;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK})
public class SessionActivityTest {

    private SessionActivity activity;

    @Mock
    DatabaseHelper mockDatabaseHelper;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
        activity = Robolectric.buildActivity(SessionActivity.class).setup().get();

        activity.db = mockDatabaseHelper;
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
        assertNotNull(activity.sessionListView);
        assertNotNull(activity.sessionAdapter);
    }

    @Test
    public void testListPopulationWithReversedOrder() {
        List<String> testSessions = Arrays.asList("Session1", "Session2", "Session3");
        when(mockDatabaseHelper.getAllSessions()).thenReturn(testSessions);
        activity.runOnUiThread(activity::loadSessions);
        assertEquals(3, activity.sessionAdapter.getCount());
        assertEquals("Session3", activity.sessionAdapter.getItem(0));
        assertEquals("Session2", activity.sessionAdapter.getItem(1));
        assertEquals("Session1", activity.sessionAdapter.getItem(2));
    }

    @Test
    public void testItemClickLaunchesCorrectActivity() {
        when(mockDatabaseHelper.getAllSessions()).thenReturn(Collections.singletonList("TestSession_123"));
        activity.runOnUiThread(activity::loadSessions);
        activity.runOnUiThread(() ->
                activity.sessionListView.performItemClick(null, 0, 0L)
        );

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertNotNull("Intent nije pokrenut", startedIntent);
        assertEquals(
                "BarcodeActivity.class",
                BarcodeActivity.class.getName(),
                startedIntent.getComponent().getClassName()
        );
        assertEquals(
                "TestSession_123",
                startedIntent.getStringExtra("SESSION_ID")
        );
    }

    @Test
    public void testEmptySessionListHandling() {
        when(mockDatabaseHelper.getAllSessions()).thenReturn(Collections.emptyList());
        activity.runOnUiThread(activity::loadSessions);

        assertEquals(0, activity.sessionAdapter.getCount());
    }
}