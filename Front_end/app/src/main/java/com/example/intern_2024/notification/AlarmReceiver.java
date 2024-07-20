package com.example.intern_2024.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.intern_2024.R;
import com.example.intern_2024.database.MQTTHelper;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Date;


public class AlarmReceiver extends BroadcastReceiver {

    MQTTHelper mqttHelper;
    String link="tuannguyen2208nat/feeds/status";

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id", 1);
        String name=intent.getStringExtra("name");
        String time=intent.getStringExtra("time");
        String state=intent.getStringExtra("state");

        String CHANNEL_ID = "channel_1";


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Automation successs "+time , NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("\"" + name + "\" "+ state);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Automation successs "+time )
                .setContentText("\"" + name + "\" "+ state)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.RED)
                .setCategory(NotificationCompat.CATEGORY_ALARM);


        notificationManager.notify(getNotificationId(), builder.build());

        if (mqttHelper == null) {
            mqttHelper = new MQTTHelper(context);
        }

        try {
            mqttHelper.sendData(link, "TEST");
        } catch (Exception e) {
            Log.e("AlarmReceiver", "Failed to send MQTT message", e);
        }

    }



    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}
