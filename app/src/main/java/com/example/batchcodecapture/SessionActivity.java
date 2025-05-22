// SessionActivity.java
package com.example.batchcodecapture;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.batchcodecapture.data.DatabaseHelper;

import java.util.Collections;
import java.util.List;

public class SessionActivity extends AppCompatActivity {
    DatabaseHelper db;
    ListView sessionListView;
    ArrayAdapter<String> sessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_session);
        db = new DatabaseHelper(this);
        sessionListView = findViewById(R.id.sessionListView);
        loadSessions();
        sessionListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSession = sessionAdapter.getItem(position);
            if (selectedSession != null){
                Intent intent = new Intent(this, BarcodeActivity.class);
                intent.putExtra("SESSION_ID", selectedSession);
                startActivity(intent);
            }
        });
    }

    void loadSessions() {
        List<String> sessions = db.getAllSessions();
        Collections.reverse(sessions);
        sessionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessions);
        sessionListView.setAdapter(sessionAdapter);
    }
}