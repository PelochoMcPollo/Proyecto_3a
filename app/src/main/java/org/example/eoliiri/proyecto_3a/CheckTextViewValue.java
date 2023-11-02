package org.example.eoliiri.proyecto_3a;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;

public class CheckTextViewValue {
    private TextView textView;
    private int consecutiveMatches = 0;
    private String previousValue = null;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int CHECK_INTERVAL = 2000; // 1 second
    private static final int CONSECUTIVE_MATCHES_THRESHOLD = 5;

    public CheckTextViewValue(TextView textView) {
        this.textView = textView;
    }

    public void startChecking() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentValue = textView.getText().toString();
                if (currentValue.equals(previousValue)) {
                    consecutiveMatches++;
                    if (consecutiveMatches == CONSECUTIVE_MATCHES_THRESHOLD) {
                        sendNotification();
                        consecutiveMatches = 0; // 重置连续匹配次数
                    }
                } else {
                    consecutiveMatches = 0;
                }
                previousValue = currentValue;
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        });
    }

    private void sendNotification() {
        Context context = textView.getContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "MyChannel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sensor no conectado o dañado");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }
}
