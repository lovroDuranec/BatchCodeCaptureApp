package com.example.batchcodecapture;


import android.content.Context;

import androidx.camera.view.PreviewView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.batchcodecapture.TestActivity;
import com.example.batchcodecapture.camera.CameraManager;
import com.example.batchcodecapture.utils.BarcodeCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class CameraManagerTest {

    private CameraManager cameraManager;
    private ActivityScenario<TestActivity> scenario;
    private BarcodeCallback mockCallback;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(TestActivity.class);
        scenario.onActivity(activity -> {
            PreviewView previewView = activity.getPreviewView();
            Context context = activity.getApplicationContext();

            mockCallback = mock(BarcodeCallback.class);

            cameraManager = new CameraManager(context, previewView, mockCallback);
        });
    }

    @Test
    public void testStartCamera_andShutdown() {
        scenario.onActivity(activity -> {
            cameraManager.startCamera();

            assertNotNull(cameraManager);
        });
        scenario.onActivity(activity -> cameraManager.shutdown());
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }
}
