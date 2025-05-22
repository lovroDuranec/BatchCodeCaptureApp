package com.example.batchcodecapture.utils;


import android.graphics.Bitmap;
import com.google.mlkit.vision.barcode.common.Barcode;

public interface BarcodeCallback {
    void onBarcodeDetected(Barcode barcode, Bitmap bitmap);
}