package com.example.batchcodecapture.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.example.batchcodecapture.utils.BarcodeCallback;
import com.example.batchcodecapture.utils.ImageUtils;
import com.example.batchcodecapture.utils.Logger;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Objects;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private BarcodeScanner barcodeScanner;
    private final BarcodeCallback callback;
    private final Logger logger;
    private int frameCounter = 0;
    private static final int FRAME_CAPTURE_RATE = 3;

    public BarcodeAnalyzer(BarcodeCallback callback, Logger logger) {
        this.callback = callback;
        this.logger = logger;
        this.barcodeScanner = BarcodeScanning.getClient();
    }

    public BarcodeAnalyzer(BarcodeCallback callback) {
        this(callback, new Logger() {
            @Override
            public void logError(String tag, String message) {

            }
        });
    }


    public void setBarcodeScanner(BarcodeScanner scanner) {
        this.barcodeScanner.close();
        this.barcodeScanner = scanner;
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

        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        InputImage inputImage = InputImage.fromMediaImage(
                Objects.requireNonNull(imageProxy.getImage()), rotationDegrees);

        barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    Bitmap bitmap = getBitmapFromImageProxy(imageProxy);
                    Bitmap rotatedBitmap = ImageUtils.rotateBitmap(bitmap, rotationDegrees);

                    for (Barcode barcode : barcodes) {
                        callback.onBarcodeDetected(barcode, rotatedBitmap);
                    }
                })
                .addOnFailureListener(e ->
                        logger.logError("BarcodeAnalyzer", "Error: " + e.getMessage()))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    protected Bitmap getBitmapFromImageProxy(ImageProxy imageProxy) {
        return imageProxy.toBitmap();
    }


    public void close() {
        barcodeScanner.close();
    }


}
