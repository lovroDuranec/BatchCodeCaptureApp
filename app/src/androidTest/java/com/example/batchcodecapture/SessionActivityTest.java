package com.example.batchcodecapture;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.example.batchcodecapture.data.DatabaseHelper;

@RunWith(AndroidJUnit4.class)
public class SessionActivityTest {

    private ActivityScenario<SessionActivity> scenario;

    @Mock
    DatabaseHelper mockDatabaseHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Intents.init();

        scenario = ActivityScenario.launch(SessionActivity.class);
        scenario.onActivity(activity -> activity.db = mockDatabaseHelper);
    }

    @After
    public void tearDown() {
        Intents.release();
        scenario.close();
    }

    @Test
    public void testActivityCreation() {
        scenario.onActivity(activity -> {
            assertNotNull(activity);
            assertNotNull(activity.sessionListView);
            assertNotNull(activity.sessionAdapter);
        });
    }

    @Test
    public void testListPopulationWithReversedOrder() {
        List<String> testSessions = Arrays.asList("Session1", "Session2", "Session3");
        when(mockDatabaseHelper.getAllSessions()).thenReturn(testSessions);

        scenario.onActivity(activity -> {
            activity.loadSessions();
            assertEquals(3, activity.sessionAdapter.getCount());
            assertEquals("Session3", activity.sessionAdapter.getItem(0));
            assertEquals("Session2", activity.sessionAdapter.getItem(1));
            assertEquals("Session1", activity.sessionAdapter.getItem(2));
        });
    }

    @Test
    public void testEmptySessionListHandling() {
        when(mockDatabaseHelper.getAllSessions()).thenReturn(Collections.emptyList());

        scenario.onActivity(activity -> {
            activity.loadSessions();
            assertEquals(0, activity.sessionAdapter.getCount());
        });
    }

    @Test
    public void testItemClickLaunchesCorrectActivity() {
        when(mockDatabaseHelper.getAllSessions()).thenReturn(Collections.singletonList("TestSession_123"));

        scenario.onActivity(activity -> {
            activity.loadSessions();
            activity.sessionListView.performItemClick(null, 0, 0L);
        });

        intended(hasComponent(BarcodeActivity.class.getName()));
        intended(hasExtra("SESSION_ID", "TestSession_123"));
    }
}