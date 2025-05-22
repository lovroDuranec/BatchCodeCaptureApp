package com.example.batchcodecapture;


import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.mlkit.vision.barcode.common.Barcode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ScanningActivityTest {

    private ActivityScenario<ScanningActivity> scenario;

    @Before
    public void setup() {
        // Launch the activity before each test
        scenario = ActivityScenario.launch(ScanningActivity.class);
    }

    @After
    public void tearDown() {
        // Close after each test
        scenario.close();
    }

    @Test
    public void exitButton_closesActivity() {
        ActivityScenario<ScanningActivity> scenario = ActivityScenario.launch(ScanningActivity.class);

        scenario.onActivity(activity -> {
            activity.findViewById(R.id.exitButton).performClick();
        });

        assertEquals(Lifecycle.State.DESTROYED, scenario.getState());
    }



    @Test
    public void whenCameraPermissionDenied_toastShows() {
        scenario.onActivity(activity -> {
            activity.onRequestPermissionsResult(ScanningActivity.CAMERA_REQUEST_CODE,
                    new String[]{Manifest.permission.CAMERA},
                    new int[]{PackageManager.PERMISSION_DENIED});
        });
    }

    @Test
    public void whenCameraPermissionGranted_cameraStarts() {
        scenario.onActivity(activity -> {
            activity.onRequestPermissionsResult(ScanningActivity.CAMERA_REQUEST_CODE,
                    new String[]{Manifest.permission.CAMERA},
                    new int[]{PackageManager.PERMISSION_GRANTED});

        });
    }

    @Test
    public void onBarcodeScanned_addsNotificationAndSaves() throws InterruptedException {
        String barcodeValue = "TEST1234";
        CountDownLatch latch = new CountDownLatch(1);

        ActivityScenario<ScanningActivity> scenario = ActivityScenario.launch(ScanningActivity.class);

        scenario.onActivity(activity -> {
            Bitmap dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Barcode barcode = createFakeBarcode(barcodeValue);
            activity.onBarcodeScanned(barcode, dummyBitmap);
            new Handler(Looper.getMainLooper()).postDelayed(latch::countDown, 500);
        });

        assertTrue("Timed out waiting for notification", latch.await(1, TimeUnit.SECONDS));

        scenario.onActivity(activity -> {
            LinearLayout notificationContainer = activity.findViewById(R.id.notificationContainer);

            boolean found = false;
            for (int i = 0; i < notificationContainer.getChildCount(); i++) {
                View view = notificationContainer.getChildAt(i);
                if (view instanceof TextView) {
                    String text = ((TextView) view).getText().toString();
                    System.out.println("Notification[" + i + "] = " + text);
                    if (text.contains(barcodeValue)) {
                        found = true;
                        break;
                    }
                }
            }

            assertTrue("Expected notification containing " + barcodeValue + " not found", found);
        });
    }

    private Barcode createFakeBarcode(String value) {
        Barcode barcode = mock(Barcode.class);
        when(barcode.getRawValue()).thenReturn(value);
        return barcode;
    }
}
