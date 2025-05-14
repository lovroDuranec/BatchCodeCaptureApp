package com.example.batchcodecapture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P}, manifest = Config.NONE)
public class BarcodeAdapterTest {

    private BarcodeActivity.BarcodeAdapter adapter;
    private List<BarcodeEntry> entries;

    @Before
    public void setUp() {
        entries = new ArrayList<>();
        adapter = new BarcodeActivity.BarcodeAdapter(
                ApplicationProvider.getApplicationContext(),
                entries
        );
    }

    @Test
    public void getView_SetsBarcodeTextCorrectly() {
        BarcodeEntry entry = new BarcodeEntry("123456", null);
        entries.add(entry);

        View view = adapter.getView(0, null, null);
        TextView textView = view.findViewById(R.id.barcodeText);
        assertEquals("123456", textView.getText().toString());
    }

    @Test
    public void getView_WithNullImagePath_SetsDefaultImage() {
        BarcodeEntry entry = new BarcodeEntry("data", null);
        entries.add(entry);

        View view = adapter.getView(0, null, null);
        ImageView imageView = view.findViewById(R.id.barcodeImage);
        assertNotNull(imageView.getDrawable());
    }

    @Test
    public void getView_WithInvalidImagePath_SetsDefaultImage() {
        BarcodeEntry entry = new BarcodeEntry("data", "/invalid/path");
        entries.add(entry);

        View view = adapter.getView(0, null, null);
        ImageView imageView = view.findViewById(R.id.barcodeImage);
        assertNotNull(imageView.getDrawable());
    }

    @Test
    public void getView_WithValidImagePath_SetsBitmap() throws IOException {
        File tempFile = File.createTempFile("test", ".png");
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }

        BarcodeEntry entry = new BarcodeEntry("data", tempFile.getAbsolutePath());
        entries.add(entry);

        View view = adapter.getView(0, null, null);
        ImageView imageView = view.findViewById(R.id.barcodeImage);
        Bitmap resultBitmap = ((android.graphics.drawable.BitmapDrawable) imageView.getDrawable()).getBitmap();
        assertNotNull(resultBitmap);

        tempFile.delete();
    }
}