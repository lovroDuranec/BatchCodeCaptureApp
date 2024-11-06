package com.example.batchcodecapture;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SessionActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private ListView sessionListView;
    private ArrayAdapter sessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_session);
        db = new DatabaseHelper(this);
        sessionListView = findViewById(R.id.sessionListView);

        List<String> sessions = db.getAllSessions();
        sessionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessions);
        sessionListView.setAdapter(sessionAdapter);



    }
}
