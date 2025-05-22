package com.example.batchcodecapture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.mlkit.vision.barcode.common.Barcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static String captureBarcodeImage(Context context, Barcode barcode, Bitmap bitmap) {
        Rect bounds = barcode.getBoundingBox();
        if (bounds != null && bitmap != null) {
            Bitmap croppedBitmap = cropBitmap(bitmap, bounds);
            if (croppedBitmap != null) {
                return saveImageToFile(context, croppedBitmap);
            }
        }
        return null;
    }

    private static Bitmap cropBitmap(Bitmap bitmap, Rect bounds) {
        try {
            int left = Math.max(0, bounds.left);
            int top = Math.max(0, bounds.top);
            int width = Math.min(bitmap.getWidth(), bounds.right) - left;
            int height = Math.min(bitmap.getHeight(), bounds.bottom) - top;
            return Bitmap.createBitmap(bitmap, left, top, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String saveImageToFile(Context context, Bitmap bitmap) {
        File imageFile = new File(context.getFilesDir(), "barcode_image_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
