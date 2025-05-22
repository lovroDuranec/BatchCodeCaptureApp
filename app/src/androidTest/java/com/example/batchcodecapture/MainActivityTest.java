package com.example.batchcodecapture;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testScanButtonLaunchesScanningActivity() {
        onView(withId(R.id.button_move_to_scanning)).perform(click());
        intended(hasComponent(ScanningActivity.class.getName()));
    }

    @Test
    public void testStorageButtonLaunchesSessionActivity() {
        onView(withId(R.id.button_move_to_storage)).perform(click());
        intended(hasComponent(SessionActivity.class.getName()));
    }

}