package com.example.batchcodecapture;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.batchcodecapture.R;
import com.example.batchcodecapture.TestActivity;
import com.example.batchcodecapture.ui.NotificationHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NotificationHelperTest {

    @Rule
    public ActivityScenarioRule<TestActivity> activityScenarioRule =
            new ActivityScenarioRule<>(TestActivity.class);

    @Test
    public void testShowStackedNotification_addsNotificationView() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            LinearLayout container = activity.findViewById(R.id.test_container);
            container.removeAllViews();
            NotificationHelper helper = new NotificationHelper(activity, container);

            String message = "Espresso Test Notification";
            helper.showStackedNotification(message);

            assertEquals(1, container.getChildCount());

            TextView view = (TextView) container.getChildAt(0);
            assertEquals(message, view.getText().toString());
        });
    }
}
