package com.example.batchcodecapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.batchcodecapture.data.BarcodeEntry;
import com.example.batchcodecapture.data.DatabaseHelper;

import org.jetbrains.annotations.Nullable;

public class BarcodeActivity extends AppCompatActivity {

    DatabaseHelper db;
    private ListView barcodeListView;
    BarcodeAdapter barcodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        db = new DatabaseHelper(this);
        barcodeListView = findViewById(R.id.barcodeListView);

        String sessionId = getIntent().getStringExtra("SESSION_ID");
        List<BarcodeEntry> barcodeEntries = db.getBarcodesForSession(sessionId);

        barcodeAdapter = new BarcodeAdapter(this, barcodeEntries);
        barcodeListView.setAdapter(barcodeAdapter);
    }


    static class BarcodeAdapter extends ArrayAdapter<BarcodeEntry> {

        BarcodeAdapter(Context context, List<BarcodeEntry> entries) {
            super(context, 0, entries);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            BarcodeEntry entry = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.barcode_list_item, parent, false);
            }

            TextView barcodeText = convertView.findViewById(R.id.barcodeText);
            ImageView barcodeImage = convertView.findViewById(R.id.barcodeImage);

            barcodeText.setText(entry.getBarcodeData());

            if (entry.getImagePath() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(entry.getImagePath());
                if (bitmap != null) {
                    barcodeImage.setImageBitmap(bitmap);
                } else {
                    barcodeImage.setImageResource(R.drawable.logo2);
                }
            } else {
                barcodeImage.setImageResource(R.drawable.logo2);
            }

            return convertView;
        }
    }
}
