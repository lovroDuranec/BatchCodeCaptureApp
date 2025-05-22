package com.example.batchcodecapture.camera;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Objects;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private final BarcodeScanner barcodeScanner;
    private final BarcodeCallback callback;
    private int frameCounter = 0;
    private static final int FRAME_CAPTURE_RATE = 3;

    public interface BarcodeCallback {
        void onBarcodeDetected(Barcode barcode, Bitmap bitmap);
    }

    public BarcodeAnalyzer(BarcodeCallback callback) {
        this.callback = callback;
        this.barcodeScanner = BarcodeScanning.getClient();
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        frameCounter++;
        if (frameCounter % FRAME_CAPTURE_RATE != 0) {
            imageProxy.close();
            return;
        }

        if (frameCounter >= Integer.MAX_VALUE - 1) {
            frameCounter = 0;
        }

        InputImage inputImage = InputImage.fromMediaImage(
                Objects.requireNonNull(imageProxy.getImage()),
                imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    Bitmap bitmap = imageProxy.toBitmap();
                    for (Barcode barcode : barcodes) {
                        callback.onBarcodeDetected(barcode, bitmap);
                    }
                })
                .addOnFailureListener(e -> Log.e("BarcodeAnalyzer", "Error: " + e.getMessage()))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    public void close() {
        barcodeScanner.close();
    }
}
