package com.example.batchcodecapture;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        previewView = findViewById(R.id.viewFinder);
        setPreviewViewLayout();

        cameraExecutor = Executors.newSingleThreadExecutor();

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

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> takePhoto());
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

        runOnUiThread(() -> Toast.makeText(this, "Barcode detected: " + barcodeData, Toast.LENGTH_SHORT).show()
        );
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

    private void takePhoto(){
        if (imageCapture == null) return;

        File photoFile = new File(getExternalFilesDir(null), "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> Toast.makeText(ScanningActivity.this, "Photo captured successfully!", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(@NonNull ImageCaptureException e) {
                runOnUiThread(() ->
                        Toast.makeText(ScanningActivity.this, e.getMessage() + "takePhoto error", Toast.LENGTH_LONG).show()
                );
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        barcodeScanner.close();
    }
}
