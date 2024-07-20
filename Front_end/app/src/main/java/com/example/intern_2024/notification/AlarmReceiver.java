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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.intern_2024.R;
import com.example.intern_2024.database.MQTTWorker;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {

    private SQLiteHelper db;
    String link = "tuannguyen2208nat/feeds/status";

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id", 1);
        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        int mode = intent.getIntExtra("mode", 0);
        String databaseName = intent.getStringExtra("databaseName");

        String state = (mode == 1) ? "ON" : "OFF";

        db = new SQLiteHelper(context, databaseName);

        String CHANNEL_ID = "channel_1";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Automation successs " + time, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("\"" + name + "\" " + state);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Automation successs " + time)
                .setContentText("\"" + name + "\" " + state)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.RED)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        notificationManager.notify(getNotificationId(), builder.build());
        sendhistory(time, name, state);

        Data data = new Data.Builder()
                .putString(MQTTWorker.EXTRA_LINK, link)
                .putString(MQTTWorker.EXTRA_MESSAGE, String.valueOf(id))
                .putString(MQTTWorker.EXTRA_STATE, state)
                .build();

        OneTimeWorkRequest mqttWorkRequest = new OneTimeWorkRequest.Builder(MQTTWorker.class)
                .setInitialDelay(1, TimeUnit.SECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(mqttWorkRequest);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        db.addItem(item);
    }

    private void sendhistory(String time, String name, String state) {
        Calendar calendar = Calendar.getInstance();
        String[] timeParts_on = time.split(":");
        int hour = Integer.parseInt(timeParts_on[0].trim());
        int minute = Integer.parseInt(timeParts_on[1].trim());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String shour = String.valueOf(hour);
        String sminute = String.valueOf(minute);
        String sday = String.valueOf(day);
        String smonth = String.valueOf(month);
        String syear = String.valueOf(year);
        if (hour < 10) {
            shour = "0" + shour;
        }
        if (minute < 10) {
            sminute = "0" + sminute;
        }
        String timePicker = sday + "/" + smonth + "/" + syear + "-" + shour + ":" + sminute;

        String detail = "Automation " + "\"" + name + "\" starts to " + state;

        addItemAndReload(timePicker, detail);
    }
}