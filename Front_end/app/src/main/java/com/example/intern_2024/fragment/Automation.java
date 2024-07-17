package com.example.intern_2024.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.AutoAdapter;
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.adapter.RelayAdapter;
import com.example.intern_2024.database.MQTTHelper;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.State;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Automation extends Fragment {

    MQTTHelper mqttHelper;
    String link="tuannguyen2208nat/feeds/status";
    private View view;
    private RecyclerView rcvAuto;
    private AutoAdapter mAutoAdapter;
    FloatingActionButton auto_add;
    private List<list_auto>  mListAuto;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ImageView close_button;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_automation, container, false);
        rcvAuto=view.findViewById(R.id.rcv_auto);
        auto_add=view.findViewById(R.id.auto_add);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvAuto.setLayoutManager(linearLayoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database= FirebaseDatabase.getInstance();
        mListAuto=new ArrayList<>();
        mqttHelper = new MQTTHelper(getContext());

        start();

        return view;

    }

    private void start() {
        getFileDatabase();

        mAutoAdapter = new AutoAdapter(mListAuto, new AutoAdapter.IClickListener() {
            @Override
            public void onClickEditAuto(list_auto auto) {}

            @Override
            public void onClickDeleteAuto(list_auto auto) {}

            @Override
            public void onClickUseAuto(list_auto auto, State state) {
                auto_switch(auto,state);
            }
        });

        rcvAuto.setAdapter(mAutoAdapter);
        auto_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAddAuto();
            }
        });

        if (user != null) {
            getlistAuto();
        }

    }

    private void getlistAuto() {
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listAuto";
        myRef = database.getReference(index);
        Query query=myRef.orderByChild("index");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_auto listAuto = dataSnapshot.getValue(list_auto.class);
                if (listAuto != null ) {
                    mListAuto.add(listAuto);
                    mAutoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_auto listAuto = dataSnapshot.getValue(list_auto.class);
                if (listAuto == null || mListAuto == null || mListAuto.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListAuto.size(); i++) {
                    if (listAuto.getIndex() == mListAuto.get(i).getIndex()) {
                        mListAuto.set(i, listAuto);
                        break;
                    }
                }
                mAutoAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list_auto listAuto = dataSnapshot.getValue(list_auto.class);
                if (listAuto == null || mListAuto == null || mListAuto.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListAuto.size(); i++) {
                    if (listAuto.getIndex() == mListAuto.get(i).getIndex()) {
                        mListAuto.remove(mListAuto.get(i));
                        break;
                    }
                }
                mAutoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }







    public void auto_switch(list_auto list_auto,State state){
        String value="";
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listAuto" ;
        String auto_id=String.valueOf(list_auto.getIndex());
        String name=list_auto.getName();
        myRef = database.getReference(index);
        String int_fix="";
        String switch_state="";

        if (state.equals(State.LEFT)) {
            if(Integer.valueOf(auto_id)<10)
            {
                int_fix="0"+auto_id;
            }
            else {
                int_fix=auto_id;
            }
            switch_state="OFF";

        }
        if (state.equals(State.RIGHT)) {
            if(Integer.valueOf(auto_id)<10)
            {
                int_fix="0"+auto_id;
            }
            else {
                int_fix=auto_id;
            }
            switch_state="ON";
        }
        value="!AUTO"+int_fix+":"+switch_state+"#";
        befor_addItemAndReload("Auto "+name+" "+switch_state+" .");
        sendDataMQTT(link,value);
    }

    private void openDialogAddAuto(){
        EditText relay_id,set_name_device;
        Button button_add;

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_add_relay);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

        relay_id=dialog.findViewById(R.id.relay_id);
        set_name_device=dialog.findViewById(R.id.set_name_device);
        button_add=dialog.findViewById(R.id.button_add);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relay_id.getText().toString().isEmpty() ) {
                    showAlert("Please enter relay id");
                    return;
                }

                int relay_id_int = Integer.parseInt(relay_id.getText().toString());
                if ( relay_id_int < 1) {
                    showAlert("Relay id must be greater than 0");
                    return;
                }
                if ( relay_id_int > 32) {
                    showAlert("Currently there are only 32 relays");
                    return;
                }
                String set_name_device_str = set_name_device.getText().toString().isEmpty() ?
                        "Relay_" + relay_id_int : set_name_device.getText().toString();

                String uid = user.getUid();
                String index = "user_inform/" + uid + "/listRelay";
                myRef = database.getReference(index);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int maxId = 0;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            list_relay relay = child.getValue(list_relay.class);
                            if(relay_id_int==relay.getRelay_id())
                            {
                                showAlert("Relay id already exists");
                                return;
                            }
                            if (relay != null && relay.getIndex() > maxId) {
                                maxId = relay.getIndex();
                            }
                        }
                        int newId = maxId + 1;
                        list_relay newRelay = new list_relay(newId, relay_id_int,set_name_device_str);
                        myRef.child(String.valueOf(newId) ).setValue(newRelay, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                befor_addItemAndReload("Add new relay "+ newRelay.getRelay_id()+" .");
                                Toast.makeText(getContext(), "Add relay successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showAlert("Failed to read data: " + error.getMessage());
                    }
                });

            }
        });

    }




    private void showAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Attention")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        long id = db.addItem(item);
        if (id != -1) {
            loadData();
        }
    }

    private void loadData() {
        adapter = new RecycleViewAdapter();
        List<Item> list = db.getAll();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private void befor_addItemAndReload(String detail) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month= calendar.get(Calendar.MONTH) + 1;
        int year= calendar.get(Calendar.YEAR);
        String shour = String.valueOf(hour);
        String sminute = String.valueOf(minute);
        String sday=String.valueOf(day);
        String smonth=String.valueOf(month);
        String syear=String.valueOf(year);
        if(hour<10)
        {
            shour="0"+shour;
        }
        if(minute<10)
        {
            sminute="0"+sminute;
        }
        String timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
        addItemAndReload(timePicker, detail);
    }


    private void getFileDatabase(){
        if (user != null) {
            String uid = user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference("user_inform").child(uid).child("file");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String databaseName = dataSnapshot.getValue(String.class);

                    if (databaseName != null && !databaseName.isEmpty()) {
                        db = new SQLiteHelper(getContext(), databaseName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }
}