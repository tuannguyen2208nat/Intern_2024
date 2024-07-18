package com.example.intern_2024.fragment.Automation;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.AutoAdapter;
import com.example.intern_2024.adapter.RecycleViewAdapter;
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
    private SharedViewModel sharedViewModel;
    MQTTHelper mqttHelper;
    String link="tuannguyen2208nat/feeds/status";
    private View view;
    private RecyclerView rcvAuto;
    private AutoAdapter mAutoAdapter;
    FloatingActionButton auto_add;
    private List<list_auto>  mListAuto;
    private List<list_relay> mListRelay;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ImageView close_button;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;
    Button btn_add;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_automation, container, false);
        rcvAuto=view.findViewById(R.id.rcv_auto);
        auto_add=view.findViewById(R.id.auto_add);
        btn_add=view.findViewById(R.id.btn_add);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvAuto.setLayoutManager(linearLayoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database= FirebaseDatabase.getInstance();
        mListAuto=new ArrayList<>();
        mListRelay=new ArrayList<>();
        mqttHelper = new MQTTHelper(getContext());
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.GetListAuto().observe(getViewLifecycleOwner(), new Observer<list_auto>() {
            @Override
            public void onChanged(list_auto s) {
                if (s != null) {
                    mListAuto.add(s);
                }
            }
        });

        start();

        return view;

    }

    private void start() {

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iterating through mListAuto to display relay names
                int i=0;
                for (list_auto test1 : mListAuto) {
                    Toast.makeText(getContext(), "Index: " + i, Toast.LENGTH_SHORT).show();
                    List<list_relay> test = test1.getListRelays();
                    for (list_relay relay : test) {
                        Toast.makeText(getContext(), "Relay Name: " + relay.getName(), Toast.LENGTH_SHORT).show();
                    }
                    i++;
                }
            }
        });


        getFileDatabase();

        if (user != null) {
            getlistRelay();
            getlistAuto();
        }

        mAutoAdapter = new AutoAdapter(mListAuto, new AutoAdapter.IClickListener() {
            @Override
            public void onClickEditAuto(list_auto auto) {
                updateDatatoList_Automation();
            }

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
        if(mListRelay==null){
            Toast.makeText(getContext(), "Plesae add relay", Toast.LENGTH_SHORT).show();
        }

        ArrayList<list_relay> send = new ArrayList<>(mListRelay);

        for (list_relay relay : send) {
            relay.setChecked(false);
        }

        sharedViewModel.SetListRelay(send);

        List_Automation fragmentB = new List_Automation();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragmentB);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

    private void  updateDatatoList_Automation(){

    }
}