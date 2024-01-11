package org.example.eoliiri.proyecto_3a;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CO2NotificationManager {

    private static final String CHANNEL_ID = "co2_channel";
    private static final int NOTIFICATION_ID = 1;

    public static void showCO2AlertNotification(Context context, int co2Value) {
        // 获取当前时间
        String currentTime = getCurrentTime();
        // 获取GPS坐标
        String gpsCoordinates = getGpsCoordinates(context);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("CO2 Alert!")
                .setContentText("CO2 level is above 500 at " + currentTime + ". GPS Coordinates: " + gpsCoordinates)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

// 添加声音
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        builder.setSound(alarmSound);

// 添加额外文本信息
        String additionalInfo = "CO2 level is above 500 at " + currentTime + ". GPS Coordinates: " + gpsCoordinates;
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(additionalInfo));

// 显示通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }


    private static void createNotificationChannel(Context context) {
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CO2 Channel";
            String description = "Channel for CO2 alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // 注册通知渠道
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private static String getGpsCoordinates(Context context) {
        // 获取定位服务
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // 检查定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有定位权限，返回一个默认的坐标
            return "latitude, longitude";
        }

        // 获取最后一次知道的位置
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            // 获取经纬度
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();

            // 返回格式化的坐标字符串
            return String.format("%f, %f", latitude, longitude);
        } else {
            // 如果获取不到位置信息，返回默认坐标
            return "latitude, longitude";
        }
    }
}
