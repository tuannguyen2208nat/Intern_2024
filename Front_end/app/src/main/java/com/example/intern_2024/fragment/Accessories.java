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

import android.view.Gravity;
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
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.adapter.RelayAdapter;
import com.example.intern_2024.database.MQTTHelper;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class Accessories extends Fragment {
    MQTTHelper mqttHelper;
    String link="tuannguyen2208nat/feeds/status";
    private View view;
    private RecyclerView rcvRelay;
    private RelayAdapter mRelayAdapter;
    private List<list_relay> mListRelay;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FloatingActionButton relay_add;
    ImageView close_button;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accessories, container, false);
        rcvRelay=view.findViewById(R.id.rcv_relay);
        relay_add=view.findViewById(R.id.relay_add);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance();
        mListRelay=new ArrayList<>();
        mqttHelper = new MQTTHelper(getContext());

        start();

        return view;
    }

    private void start(){
        getFileDatabase();
        mRelayAdapter = new RelayAdapter(mListRelay, new RelayAdapter.IClickListener() {
            @Override
            public void onClickUpdateRelay(list_relay relay) {
                openDialogUpdateRelay(relay);
            }

            @Override
            public void onClickDeleteRelay(list_relay relay) {deleteRelay(relay);}

            @Override
            public void onClickUseRelay(list_relay relay, State state) {
                relay_switch(relay,state);
            }
        });
        rcvRelay.setAdapter(mRelayAdapter);
        relay_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAddRelay();
            }
        });

        if (user != null) {
            getlistRelay();
        }

    }

    private void getlistRelay() {
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listRelay";
        myRef = database.getReference(index);
        Query query=myRef.orderByChild("index");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay != null ) {
                    mListRelay.add(listRelay);
                    mRelayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay == null || mListRelay == null || mListRelay.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListRelay.size(); i++) {
                    if (listRelay.getIndex() == mListRelay.get(i).getIndex()) {
                        mListRelay.set(i, listRelay);
                        break;
                    }
                }
                mRelayAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay == null || mListRelay == null || mListRelay.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListRelay.size(); i++) {
                    if (listRelay.getIndex() == mListRelay.get(i).getIndex()) {
                        mListRelay.remove(mListRelay.get(i));
                        break;
                    }
                }
                mRelayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openDialogUpdateRelay(list_relay list_relay) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_change_name_relay);
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

        EditText change_name_device = dialog.findViewById(R.id.change_name_device);
        Button button_name_device = dialog.findViewById(R.id.button_name_device);
        button_name_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = change_name_device.getText().toString();
                list_relay.setName(newName);
                String relay_id=String.valueOf(list_relay.getRelay_id());
                myRef.child(String.valueOf(list_relay.getIndex())).updateChildren(list_relay.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        befor_addItemAndReload("Update name relay "+relay_id+" .");
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void relay_switch(list_relay list_relay,State state){
        String value="";
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listRelay" ;
        String relay_id=String.valueOf(list_relay.getRelay_id());
        myRef = database.getReference(index);
        String int_fix="";
        String switch_state="";

        if (state.equals(State.LEFT)) {
            if(Integer.valueOf(relay_id)<10)
            {
                int_fix="0"+relay_id;
            }
            else {
                int_fix=relay_id;
            }
            switch_state="OFF";

        }
        if (state.equals(State.RIGHT)) {
            if(Integer.valueOf(relay_id)<10)
            {
                int_fix="0"+relay_id;
            }
            else {
                int_fix=relay_id;
            }
            switch_state="ON";
        }
        value="!RELAY"+int_fix+":"+switch_state+"#";
        befor_addItemAndReload("Relay "+relay_id+" "+switch_state+" .");
        sendDataMQTT(link,value);
    }


    public void deleteRelay(list_relay list_relay){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_delete_relay);
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

        Button button_no=dialog.findViewById(R.id.button_no);
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button button_yes=dialog.findViewById(R.id.button_yes);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = user.getUid();
                String index = "user_inform/" + uid + "/listRelay" ;
                String relay_id=String.valueOf(list_relay.getRelay_id());
                myRef = database.getReference(index);
                myRef.child(String.valueOf(list_relay.getIndex())).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        befor_addItemAndReload("Delete relay "+relay_id+" .");
                        Toast.makeText(getContext(), "Delete relay successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
    }

    private void openDialogAddRelay(){
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