package org.example.eoliiri.proyecto_3a;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static void mostrarNotificacion(Context context, String titulo, String contenido) {
        // 创建通知通道（适用于 Android 8.0 及以上）
        createNotificationChannel(context);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_id")
                .setSmallIcon(R.drawable.logo)  // 设置通知图标
                .setContentTitle(titulo)  // 设置通知标题
                .setContentText(contenido)  // 设置通知内容
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contenido))  // 设置 BigTextStyle，允许展开通知查看完整文本内容
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);  // 设置通知优先级

        // 显示通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    // 创建通知通道
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("canal_id", name, importance);
            channel.setDescription(description);

            // 注册通知通道
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
