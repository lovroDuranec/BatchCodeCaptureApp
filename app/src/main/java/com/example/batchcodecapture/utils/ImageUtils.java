package com.example.batchcodecapture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
            int  targetAspectRatio = 2;
            int padding = 20;
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            int origWidth = bounds.width() + 2 * padding;
            int origHeight = bounds.height() + 2 * padding;
            int targetWidth;
            int targetHeight;

            if (origWidth / (float) origHeight > targetAspectRatio) {
                targetWidth = origWidth;
                targetHeight = Math.round((float) targetWidth / targetAspectRatio);
            } else {
                targetHeight = origHeight;
                targetWidth = Math.round((float)targetHeight * targetAspectRatio);
            }
            int left = centerX - targetWidth / 2;
            int top = centerY - targetHeight / 2;
            left = Math.max(0, left);
            top = Math.max(0, top);
            if (left + targetWidth > bitmap.getWidth()) {
                targetWidth = bitmap.getWidth() - left;
            }
            if (top + targetHeight > bitmap.getHeight()) {
                targetHeight = bitmap.getHeight() - top;
            }
            return Bitmap.createBitmap(bitmap, left, top, targetWidth, targetHeight);
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

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
