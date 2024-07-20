package com.example.intern_2024.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MQTTWorker extends Worker {

    public static final String TAG = "MQTTWorker";
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_MESSAGE = "message";
        public static final String EXTRA_STATE = "state";
    private list_auto listAuto = new list_auto();
    private List<list_relay> listRelays ;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String ID;
    MQTTHelper mqttHelper;

    public MQTTWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mqttHelper = new MQTTHelper(context);
        ID = getInputData().getString(EXTRA_MESSAGE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        getlistAuto();
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String link = getInputData().getString(EXTRA_LINK);
        String state = getInputData().getString(EXTRA_STATE);

        if (ID == null || link == null || user == null) {
            return Result.failure();
        }

        if (listAuto == null || listAuto.getListRelays() == null) {
            return Result.failure();
        }

        listRelays=new ArrayList<>(listAuto.getListRelays());

        for (list_relay listRelay : listRelays) {
            String int_fix="";
            String relay_id=String.valueOf(listRelay.getRelay_id());

            if(Integer.valueOf(relay_id)<10)
            {
                int_fix="0"+relay_id;
            }
            else {
                int_fix=relay_id;
            }
            String value="!RELAY"+int_fix+":"+state+"#";
            Log.d("Value", "doWork: " + value);
            mqttHelper.sendData(link, value);
        }

        return Result.success();
    }

    private void getlistAuto() {
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listAuto";
        myRef = database.getReference(index);
        myRef.child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listAuto = dataSnapshot.getValue(list_auto.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }
}
