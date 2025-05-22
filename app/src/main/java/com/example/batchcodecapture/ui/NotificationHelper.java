package com.example.batchcodecapture.ui;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.batchcodecapture.R;

public class NotificationHelper {

    private final Context context;
    private final LinearLayout container;

    public NotificationHelper(Context context, LinearLayout container) {
        this.context = context;
        this.container = container;
    }

    public void showStackedNotification(String message) {
        TextView notificationView = new TextView(context);
        notificationView.setText(message);
        notificationView.setBackgroundResource(R.drawable.notification_background);
        notificationView.setTextColor(Color.WHITE);
        notificationView.setPadding(12, 12, 12, 12);

        container.addView(notificationView);
        notificationView.setAlpha(0f);
        notificationView.animate().alpha(1f).setDuration(300).start();
        notificationView.postDelayed(() ->
                notificationView.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> container.removeView(notificationView)).start(), 3000);
    }
}
