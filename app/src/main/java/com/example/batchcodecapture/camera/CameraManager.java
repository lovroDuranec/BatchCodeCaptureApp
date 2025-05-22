package com.example.batchcodecapture.camera;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraManager {

    private final Context context;
    private final PreviewView previewView;
    private final BarcodeAnalyzer barcodeAnalyzer;
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    public CameraManager(Context context, PreviewView previewView, BarcodeAnalyzer.BarcodeCallback callback) {
        this.context = context;
        this.previewView = previewView;
        this.barcodeAnalyzer = new BarcodeAnalyzer(callback);
    }

    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindToLifecycle(cameraProvider);
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindToLifecycle(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, barcodeAnalyzer);

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) context,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview, imageCapture, imageAnalysis);
    }

    public void shutdown() {
        cameraExecutor.shutdown();
        barcodeAnalyzer.close();
    }
}
