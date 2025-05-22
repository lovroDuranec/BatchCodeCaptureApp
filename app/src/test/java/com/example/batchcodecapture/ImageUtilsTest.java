package com.example.batchcodecapture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.test.core.app.ApplicationProvider;

import com.example.batchcodecapture.utils.ImageUtils;
import com.google.mlkit.vision.barcode.common.Barcode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 31)
public class ImageUtilsTest {

    private Context context;
    private Bitmap bitmap;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

    @Test
    public void captureBarcodeImage_returnsNullIfBoundingBoxNull() {
        Barcode barcode = Mockito.mock(Barcode.class);
        Mockito.when(barcode.getBoundingBox()).thenReturn(null);

        String result = ImageUtils.captureBarcodeImage(context, barcode, bitmap);
        assertNull(result);
    }

    @Test
    public void captureBarcodeImage_returnsNullIfBitmapNull() {
        Barcode barcode = Mockito.mock(Barcode.class);
        Mockito.when(barcode.getBoundingBox()).thenReturn(new Rect(0, 0, 50, 50));

        String result = ImageUtils.captureBarcodeImage(context, barcode, null);
        assertNull(result);
    }

    @Test
    public void captureBarcodeImage_savesImageAndReturnsPath() {
        Barcode barcode = Mockito.mock(Barcode.class);
        Rect bounds = new Rect(10, 10, 60, 60);
        Mockito.when(barcode.getBoundingBox()).thenReturn(bounds);

        String path = ImageUtils.captureBarcodeImage(context, barcode, bitmap);

        assertNotNull(path);

        File savedFile = new File(path);
        assertTrue(savedFile.exists());
        assertTrue(savedFile.length() > 0);

        // Cleanup test file
        savedFile.delete();
    }
}
