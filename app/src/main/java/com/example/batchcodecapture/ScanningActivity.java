package com.example.batchcodecapture;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.batchcodecapture.camera.CameraManager;
import com.example.batchcodecapture.data.DatabaseHelper;
import com.example.batchcodecapture.ui.NotificationHelper;
import com.example.batchcodecapture.utils.ImageUtils;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.Manifest;

public class ScanningActivity extends AppCompatActivity {

    static final int CAMERA_REQUEST_CODE = 1001;
    private PreviewView previewView;
    private LinearLayout notificationContainer;
    private boolean checkIfNewSessionNeeded = true;

    private CameraManager cameraManager;
    NotificationHelper notificationHelper;
    DatabaseHelper db;
    ExecutorService dbExecutor;

    final HashSet<String> scannedBarcodesCache = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        previewView = findViewById(R.id.viewFinder);
        notificationContainer = findViewById(R.id.notificationContainer);

        db = new DatabaseHelper(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        notificationHelper = new NotificationHelper(this, notificationContainer);
        SharedPreferences prefs = getSharedPreferences("session_prefs", MODE_PRIVATE);
        DatabaseHelper.defaultSessionId = prefs.getInt("last_session_id", 0);
        cameraManager = new CameraManager(this, previewView, this::onBarcodeScanned);

        if (hasCameraPermission()) {
            cameraManager.startCamera();
        } else {
            requestCameraPermission();
        }

        findViewById(R.id.exitButton).setOnClickListener(v -> finish());
    }

    void onBarcodeScanned(Barcode barcode, Bitmap bitmap) {
        String barcodeData = barcode.getRawValue();
        if (barcodeData != null && !scannedBarcodesCache.contains(barcodeData)) {
            scannedBarcodesCache.add(barcodeData);

            String savedImagePath = ImageUtils.captureBarcodeImage(this, barcode, bitmap);

            dbExecutor.execute(() -> db.addentry(barcodeData, savedImagePath));
            if (checkIfNewSessionNeeded) {
                db.updateSessionID(this);
                checkIfNewSessionNeeded = false;
            }
            runOnUiThread(() -> notificationHelper.showStackedNotification(barcodeData));
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraManager.startCamera();
        } else {
            Toast.makeText(this, "Allow camera permission to use this app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.shutdown();
        dbExecutor.shutdown();
    }
}

