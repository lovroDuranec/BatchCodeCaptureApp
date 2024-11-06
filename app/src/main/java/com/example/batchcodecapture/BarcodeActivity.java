package com.example.batchcodecapture;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class BarcodeActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView barcodeListView;
    private ArrayAdapter<String> barcodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        db = new DatabaseHelper(this);
        barcodeListView = findViewById(R.id.barcodeListView);

        String sessionId = getIntent().getStringExtra("SESSION_ID");
        List<String> barcodes = db.getBarcodesForSession(sessionId);

        barcodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barcodes);
        barcodeListView.setAdapter(barcodeAdapter);
    }
}
