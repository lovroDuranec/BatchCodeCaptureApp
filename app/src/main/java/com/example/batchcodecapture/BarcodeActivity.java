package com.example.batchcodecapture;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.batchcodecapture.data.BarcodeEntry;
import com.example.batchcodecapture.data.DatabaseHelper;

import org.jetbrains.annotations.Nullable;

public class BarcodeActivity extends AppCompatActivity {

    DatabaseHelper db;
    private ListView barcodeListView;
    BarcodeAdapter barcodeAdapter;
    List<BarcodeEntry> barcodeEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        Application app = (Application) getApplicationContext();
        if (app instanceof TestApplication) {
            db = ((TestApplication) app).getDatabaseHelper();
        } else {
            db = new DatabaseHelper(this);
        }
        barcodeListView = findViewById(R.id.barcodeListView);

        String sessionId = getIntent().getStringExtra("SESSION_ID");
        barcodeEntries = db.getBarcodesForSession(sessionId);
        Collections.reverse(barcodeEntries);

        barcodeAdapter = new BarcodeAdapter(this, barcodeEntries);
        barcodeListView.setAdapter(barcodeAdapter);

        barcodeListView.setOnItemClickListener((parent, view, position, id) -> {
            BarcodeEntry entry = barcodeEntries.get(position);
            showBarcodeDialog(entry.getBarcodeData(), entry.getImagePath());
        });

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
            TextView barcodeTime = convertView.findViewById(R.id.barcodeTime);
            barcodeTime.setText(entry.getTimestamp());
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

    void showBarcodeDialog(String barcodeText, String imagePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_barcode_detail, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.popupImage);
        TextView textView = dialogView.findViewById(R.id.popupText);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        if (imagePath != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }

        textView.setText(barcodeText);
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public List<BarcodeEntry> getBarcodeEntries() {
        return barcodeEntries;
    }

}
