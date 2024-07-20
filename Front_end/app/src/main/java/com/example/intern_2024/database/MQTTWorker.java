package com.example.intern_2024.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class MQTTWorker extends Worker {
    public static final String TAG = "MQTTWorker";
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_MESSAGE = "message";

    private list_auto auto = new list_auto();
    private final List<list_relay> listRelays = new ArrayList<>();
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private int index,mode;
    String status;

    public MQTTWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();

        Context context = getApplicationContext();
        String link = getInputData().getString(EXTRA_LINK);
        String message = getInputData().getString(EXTRA_MESSAGE);

        if (message == null) {
            Log.e(TAG, "Message is null");
            return Result.failure();
        }

        try {
            index = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Message is not a valid integer", e);
            return Result.failure();
        }

        getlistAuto();

        MQTTHelper mqttHelper = new MQTTHelper(context);

        try {
            for (list_relay relay : listRelays) {
                String int_fix="";
                String switch_state="";
                String relay_id=String.valueOf(relay.getRelay_id());

                if(Integer.parseInt(relay_id)<10)
                {
                    int_fix="0"+relay_id;
                }
                else {
                    int_fix=relay_id;
                }

                if(mode==1)
                {

                    switch_state = "ON";
                }
                else
                {
                    switch_state = "OFF";
                }
                String value="!RELAY"+int_fix+":"+switch_state+"#";
                mqttHelper.sendData(link, value);
            }
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Failed to send MQTT message", e);
            return Result.failure();
        }
    }

    private void getlistAuto() {
        String uid = user.getUid();
        String indexPath = "user_inform/" + uid + "/listAuto";
        myRef = database.getReference(indexPath);
        Query query = myRef.orderByChild("index");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_auto listAuto = dataSnapshot.getValue(list_auto.class);
                if (listAuto != null && listAuto.getIndex() == index) {
                    auto = listAuto;
                    listRelays.addAll(auto.getListRelays());
                    mode=listAuto.getMode();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}
