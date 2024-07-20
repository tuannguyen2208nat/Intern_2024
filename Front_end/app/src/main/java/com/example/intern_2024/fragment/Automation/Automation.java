package com.example.intern_2024.fragment.Automation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.AutoAdapter;
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.notification.AlarmList;
import com.example.intern_2024.notification.AlarmReceiver;
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Automation extends Fragment {
    AlarmList alarmList = new AlarmList();
    private View view;
    private RecyclerView rcvAuto;
    private AutoAdapter mAutoAdapter;
    FloatingActionButton auto_add;
    private List<list_auto>  mListAuto;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;
    private AlarmManager alarmManager;
    String databaseName;

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

        start();

        return view;

    }

    private void start() {

        getFileDatabase();

        mAutoAdapter = new AutoAdapter(mListAuto, new AutoAdapter.IClickListener() {
            @Override
            public void onClickEditAuto(list_auto auto) {
                openDialogEditAuto(auto);
            }

            @Override
            public void onClickUseAuto(list_auto auto, State state) {
                auto_switch(auto,state);
            }
        });

        rcvAuto.setAdapter(mAutoAdapter);

        getlistAuto();


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

    private void openDialogAddAuto() {

        Add_Automation fragmentB = new Add_Automation();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragmentB);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openDialogEditAuto(list_auto auto) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("list_auto", (Serializable) auto);

        Edit_Automation fragmentB = new Edit_Automation();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragmentB);
        fragmentTransaction.addToBackStack(null);
        fragmentB.setArguments(bundle);
        fragmentTransaction.commit();
    }

    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        db.addItem(item);
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
                     databaseName= dataSnapshot.getValue(String.class);

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

    public void auto_switch(list_auto list_auto,State state){
        String value="";
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listAuto" ;
        String auto_name=list_auto.getName();
        myRef = database.getReference(index);
        String switch_state="";

        if (state.equals(State.LEFT)) {
            switch_state="OFF";
            cancelAlarm(list_auto);
        }
        if (state.equals(State.RIGHT)) {
            switch_state="ON";
            setAlarm(list_auto);
        }
        befor_addItemAndReload("Automation "+auto_name+" "+switch_state+" .");
    }

    public void setAlarm(list_auto auto) {
        String time = auto.getTime();
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0].trim());
        int minute = Integer.parseInt(timeParts[1].trim());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent alarmIntent = new Intent(  getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("id", auto.getIndex());
        alarmIntent.putExtra("name", auto.getName());
        alarmIntent.putExtra("time", time);
        alarmIntent.putExtra("mode", auto.getMode());
        alarmIntent.putExtra("databaseName", databaseName);

        AlarmManager alarmManager = (AlarmManager)   getContext().getSystemService(Context.ALARM_SERVICE);

        boolean contains = alarmList.containsAlarmIndex(auto.getIndex());
        System.out.println("contains: " + contains);
        if(!contains)
        {
            alarmList.addAlarmIndex(auto.getIndex());
        }

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                getContext(),
                auto.getIndex(), // Unique request code
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        }
    }

    public void cancelAlarm(list_auto auto) {
        Intent alarmIntent = new Intent(  getContext(), AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                getContext(),
                auto.getIndex(), // Unique request code
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager)   getContext().getSystemService(Context.ALARM_SERVICE);
        alarmList.removeAlarmIndex(auto.getIndex());

        if (alarmManager != null) {
            alarmManager.cancel(alarmPendingIntent);
            alarmPendingIntent.cancel();
        }
    }

    public void cancelAllAlarms() {
        List<Integer> alarmIndexes = alarmList.getAlarmIndexes();

        for (int index : alarmIndexes) {
            Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                    getContext(),
                    index, // Unique request code
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(alarmPendingIntent);
                alarmPendingIntent.cancel();
            }
        }

        alarmList.clear();
    }
}