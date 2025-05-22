package com.example.batchcodecapture;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

public class TestActivity extends AppCompatActivity {

    private PreviewView previewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setId(R.id.test_container);

        previewView = new PreviewView(this);
        previewView.setId(R.id.previewView);
        layout.addView(previewView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        setContentView(layout);
    }

    public PreviewView getPreviewView() {
        return previewView;
    }
}
