package com.example.intern_2024.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.intern_2024.database.MQTTHelper;

public class MQTTWorker extends Worker {

    public static final String TAG = "MQTTWorker";
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_MESSAGE = "message";

    public MQTTWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String link = getInputData().getString(EXTRA_LINK);
        String message = getInputData().getString(EXTRA_MESSAGE);

        MQTTHelper mqttHelper = new MQTTHelper(context);

        try {
            mqttHelper.sendData(link, message);
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Gửi tin nhắn MQTT thất bại", e);
            return Result.failure();
        }
    }
}
