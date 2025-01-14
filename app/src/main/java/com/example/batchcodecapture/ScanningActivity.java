package com.example.batchcodecapture;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.camera.core.ImageAnalysis;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;

import android.Manifest;


public class ScanningActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1001;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private int frameCounter = 0;
    private static final int FRAME_CAPTURE_RATE = 3;
    private final HashSet<String> scannedBarcodesCache = new HashSet<>();
    private DatabaseHelper db;
    private ExecutorService dbExecutor;
    private LinearLayout notificationContainer;
    private boolean checkIfNewSessionNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        db = new DatabaseHelper(this);
        notificationContainer = findViewById(R.id.notificationContainer);
        checkIfNewSessionNeeded = true;
        previewView = findViewById(R.id.viewFinder);
        setPreviewViewLayout();

        cameraExecutor = Executors.newSingleThreadExecutor();
        dbExecutor = Executors.newSingleThreadExecutor();
        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }

        //cameraX
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener( () -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindLifeCycle(cameraProvider);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));

        //cameraExecutor
        cameraExecutor = Executors.newSingleThreadExecutor();
        //barcode scanner
        barcodeScanner = BarcodeScanning.getClient();

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> finish());
    }

    private void bindLifeCycle( ProcessCameraProvider cameraProvider){
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().setTargetRotation(previewView.getDisplay().getRotation()).build();

        ImageAnalysis imageAnalysis = new  ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, imageAnalysis);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage() + "bindLifeCycle error", Toast.LENGTH_LONG).show();
        }

    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {
        frameCounter+=1;
        if (frameCounter%FRAME_CAPTURE_RATE != 0){
            imageProxy.close();
            return;
        }
        if (frameCounter == Integer.MAX_VALUE - 1){
            frameCounter = 0;
        }
        InputImage inputImage = InputImage.fromMediaImage(Objects.requireNonNull(imageProxy.getImage()), imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(inputImage).addOnSuccessListener(barcodes -> {
            for (Barcode barcode : barcodes) {
                processBarcodeResult(barcode);
            }
        }).addOnFailureListener(e ->Toast.makeText(ScanningActivity.this, e.getMessage() + "analyzeImage error", Toast.LENGTH_LONG).show()
        ).addOnCompleteListener(task ->imageProxy.close());
    }

    private void processBarcodeResult(Barcode barcode){
        String barcodeData = barcode.getRawValue();

        if (barcodeData != null && !scannedBarcodesCache.contains(barcodeData)){
            scannedBarcodesCache.add(barcodeData);
            dbExecutor.execute(() -> db.addentry((barcodeData)));
            if (checkIfNewSessionNeeded){
                db.updateSessionID();
                checkIfNewSessionNeeded = false;
            }
            runOnUiThread(() -> showStackedNotification(barcodeData));
        }
    }

    private void setPreviewViewLayout() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        previewView.setLayoutParams(layoutParams);
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
            startCamera();
        } else {
            Toast.makeText(ScanningActivity.this, "Allow camera permission to use this app", Toast.LENGTH_LONG).show();
        }
    }

    private void startCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() ->{
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindLifeCycle(cameraProvider);
            } catch (Exception e){
                Toast.makeText(ScanningActivity.this, e.getMessage() + "takePhoto error", Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void showStackedNotification(String message){
        TextView notificationView = new TextView(this);
        notificationView.setText(message);
        notificationView.setBackgroundResource(R.drawable.notification_background);
        notificationView.setTextColor(Color.WHITE);
        notificationView.setPadding(12,12,12,12);

        notificationContainer.addView(notificationView);

        notificationView.setAlpha(0f);
        notificationView.animate().alpha(1f).setDuration(300).start();
        notificationView.postDelayed(() -> notificationView.animate().alpha(0f).setDuration(300).withEndAction(() -> notificationContainer.removeView(notificationView)).start(), 3000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        dbExecutor.shutdown();
        barcodeScanner.close();
    }
}
