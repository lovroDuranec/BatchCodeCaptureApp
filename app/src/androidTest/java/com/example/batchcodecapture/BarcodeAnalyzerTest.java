package com.example.batchcodecapture;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;

import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.batchcodecapture.camera.BarcodeAnalyzer;
import com.example.batchcodecapture.utils.BarcodeCallback;
import com.example.batchcodecapture.utils.Logger;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class BarcodeAnalyzerTest {
    private BarcodeAnalyzer barcodeAnalyzer;
    private BarcodeCallback mockCallback;
    private ImageProxy mockImageProxy;

    private Logger mockLogger;

    @Before
    public void setUp() {
        mockCallback = mock(BarcodeCallback.class);
        mockLogger = mock(Logger.class);
        mockImageProxy = createMockImageProxy();

        barcodeAnalyzer = new BarcodeAnalyzer(mockCallback, mockLogger) {
            @Override
            protected Bitmap getBitmapFromImageProxy(ImageProxy imageProxy) {
                return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            }
        };
    }

    private ImageProxy createMockImageProxy() {
        ImageProxy imageProxy = mock(ImageProxy.class);
        Image image = mock(Image.class);
        ImageInfo imageInfo = mock(ImageInfo.class);

        Image.Plane[] planes = new Image.Plane[3];
        for (int i = 0; i < 3; i++) {
            Image.Plane plane = mock(Image.Plane.class);
            when(plane.getBuffer()).thenReturn(ByteBuffer.allocate(10));
            planes[i] = plane;
        }

        when(image.getFormat()).thenReturn(ImageFormat.YUV_420_888);
        when(image.getPlanes()).thenReturn(planes);
        when(imageProxy.getImage()).thenReturn(image);
        when(imageProxy.getWidth()).thenReturn(100);
        when(imageProxy.getHeight()).thenReturn(100);

        when(imageProxy.getImageInfo()).thenReturn(imageInfo);
        when(imageInfo.getRotationDegrees()).thenReturn(0);

        return imageProxy;
    }
    @Test
    public void testBarcodeDetection() {
        BarcodeScanner mockScanner = mock(BarcodeScanner.class);
        Barcode testBarcode = mock(Barcode.class);
        when(mockScanner.process(any(InputImage.class)))
                .thenReturn(Tasks.forResult(Collections.singletonList(testBarcode)));

        barcodeAnalyzer.setBarcodeScanner(mockScanner);

        for (int i = 0; i < 3; i++) {
            barcodeAnalyzer.analyze(mockImageProxy);
        }

        verify(mockCallback, timeout(1000)).onBarcodeDetected(eq(testBarcode), (Bitmap) any());
    }

    @Test
    public void testFrameSkipping() {

        barcodeAnalyzer.analyze(mockImageProxy);
        verify(mockImageProxy).close();

        barcodeAnalyzer.analyze(mockImageProxy);
        verify(mockImageProxy, times(2)).close();
    }

    @Test
    public void testAnalyze_processesFrameAndCallsCallbackOnSuccess() throws Exception {

        Barcode mockBarcode = mock(Barcode.class);
        List<Barcode> barcodes = Collections.singletonList(mockBarcode);

        BarcodeScanner mockScanner = mock(BarcodeScanner.class);
        Field scannerField = BarcodeAnalyzer.class.getDeclaredField("barcodeScanner");
        scannerField.setAccessible(true);
        scannerField.set(barcodeAnalyzer, mockScanner);

        when(mockScanner.process(any(InputImage.class)))
                .thenReturn(Tasks.forResult(barcodes));

        for (int i = 0; i < 3; i++) {
            barcodeAnalyzer.analyze(mockImageProxy);
        }

        verify(mockCallback, timeout(1000)).onBarcodeDetected(eq(mockBarcode), (Bitmap) any());
        verify(mockImageProxy, times(3)).close();
    }

    @Test
    public void testAnalyze_logsErrorOnFailure() throws Exception {
        Exception testException = new Exception("Test error");

        BarcodeScanner mockScanner = mock(BarcodeScanner.class);
        Field scannerField = BarcodeAnalyzer.class.getDeclaredField("barcodeScanner");
        scannerField.setAccessible(true);
        scannerField.set(barcodeAnalyzer, mockScanner);

        when(mockScanner.process(any(InputImage.class)))
                .thenReturn(Tasks.forException(testException));

        for (int i = 0; i < 3; i++) {
            barcodeAnalyzer.analyze(mockImageProxy);
        }

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).logError(eq("BarcodeAnalyzer"), captor.capture());
        assertTrue(captor.getValue().contains("Test error"));
    }



    @Test
    public void testClose_closesBarcodeScanner() throws Exception {
        BarcodeScanner mockScanner = mock(BarcodeScanner.class);

        barcodeAnalyzer.setBarcodeScanner(mockScanner);

        barcodeAnalyzer.close();

        verify(mockScanner).close();
    }

}