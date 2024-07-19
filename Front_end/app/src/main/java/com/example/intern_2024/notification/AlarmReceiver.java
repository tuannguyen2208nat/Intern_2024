package com.example.intern_2024.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.intern_2024.R;


import java.util.Date;


public class AlarmReceiver extends BroadcastReceiver {

    private int id;

    @Override
    public void onReceive(Context context, Intent intent) {

        id = intent.getIntExtra("id", 1);
        String CHANNEL_ID = "channel_1";


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Description for Channel 1");
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Alarm Notification")
                .setContentText("Description for Channel 1")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.RED)
                .setCategory(NotificationCompat.CATEGORY_ALARM);


        notificationManager.notify(getNotificationId(), builder.build());

    }


    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}
