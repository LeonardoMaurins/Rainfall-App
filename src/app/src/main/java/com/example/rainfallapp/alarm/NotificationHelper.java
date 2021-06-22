package com.example.rainfallapp.alarm;

import com.example.rainfallapp.forecast.ForecastActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.rainfallapp.R;

public class NotificationHelper extends ContextWrapper {

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        // Creates notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.notification_id), getString(R.string.notification_title), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(getString(R.string.notification_description));
            notificationChannel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public NotificationCompat.Builder createNotification(String location, String description, String date, String icon, String precipitation) {
        // Builds the notification
        return new NotificationCompat.Builder(this, getString(R.string.notification_id))
                .setSmallIcon(ForecastActivity.returnDrawable(icon))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), ForecastActivity.returnDrawable(icon)))
                .setContentTitle(location + " - " + description)
                .setContentText("Date: " + date + ", Precipitation: " + precipitation)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Date: " + date + ", Precipitation: " + precipitation))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(getString(R.string.notification_id))
                .setAutoCancel(true);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
