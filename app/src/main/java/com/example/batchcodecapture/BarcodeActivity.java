package com.example.batchcodecapture;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BarcodeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BarcodeAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadBarcodes();
    }

    private void loadBarcodes(){
        Cursor cursor = db.getAllBarcodes();
        adapter = new BarcodeAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }
}
