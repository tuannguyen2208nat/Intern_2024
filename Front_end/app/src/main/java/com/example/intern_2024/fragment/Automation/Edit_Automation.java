package com.example.intern_2024.fragment.Automation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.adapter.RelayAutoAdapter;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Edit_Automation extends Fragment {
    RecycleViewAdapter adapter;
    private View view;
    private RecyclerView rcvRelay;
    private RelayAutoAdapter mRelayAdapter;
    private List<list_relay> mListRelay, addRelay;
    private ImageView backIcon;
    private Button save;
    private EditText name,time;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SQLiteHelper db;
    private list_auto listAuto;
    Spinner select_mode;
    private int mode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.edit_automation, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            listAuto = (list_auto) bundle.getSerializable("list_auto");
        }

        backIcon = view.findViewById(R.id.backIcon);
        save = view.findViewById(R.id.create);
        rcvRelay = view.findViewById(R.id.rcvRelay);
        save = view.findViewById(R.id.save);
        select_mode= view.findViewById(R.id.mode);
        time=view.findViewById(R.id.time);
        name=view.findViewById(R.id.name);
        name.setText(listAuto.getName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        mListRelay = new ArrayList<>();
        addRelay = new ArrayList<>();

        setupRelayAdapter();

        String[] options = { " ","On","Off"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_mode.setAdapter(adapter);
        select_mode.setSelection(0);
        if(listAuto.getMode()==1){
            select_mode.setSelection(1);
        }else if(listAuto.getMode()==2){
            select_mode.setSelection(2);
        }
        time.setText(listAuto.getTime());

        start();

        return view;
    }

    private void start() {

        getFileDatabase();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAuto();
            }
        });

        if (user != null) {
            getlistRelay();
        }

        select_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);

                if (" ".equals(selectedOption)) {
                    mode = 0;
                } else if ("On".equals(selectedOption)) {
                    mode = 1;
                } else if ("Off".equals(selectedOption)) {
                    mode = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void SaveAuto() {
        if (listAuto.getSize() == 0) {
            Toast.makeText(getContext(), "No relays selected", Toast.LENGTH_SHORT).show();
            return;
        }
        UploadData();
    }

    private void getlistRelay() {
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listRelay";
        myRef = database.getReference(index);
        Query query = myRef.orderByChild("index");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay != null) {
                    updateRelays(listRelay);
                    mListRelay.add(listRelay);
                    mRelayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateRelays(list_relay relayA) {
        for (list_relay relayB : listAuto.getListRelays()) {
            if (relayA.getRelay_id() == relayB.getRelay_id()) {
                relayA.setChecked(true);
            }
        }
    }

    private void getFileDatabase() {
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
                    // Handle database error
                }
            });
        }
    }

    private void befor_addItemAndReload(String detail) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String shour = (hour < 10 ? "0" : "") + hour;
        String sminute = (minute < 10 ? "0" : "") + minute;
        String sday = String.valueOf(day);
        String smonth = String.valueOf(month);
        String syear = String.valueOf(year);
        String timePicker = sday + "/" + smonth + "/" + syear + "-" + shour + ":" + sminute;
        addItemAndReload(timePicker, detail);
    }

    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        db.addItem(item);
    }

    private void setupRelayAdapter() {
        mRelayAdapter = new RelayAutoAdapter(mListRelay, new RelayAutoAdapter.IClickListener() {
            @Override
            public void onClickSelectRelay(list_relay relay, boolean isChecked) {
                handleRelaySelection(relay, isChecked);
            }
        });
        rcvRelay.setAdapter(mRelayAdapter);
    }

    private void handleRelaySelection(list_relay relay, boolean isChecked) {
        boolean pass = false;
        for (list_relay item : mListRelay) {
            if (item.getRelay_id() == relay.getRelay_id()) {
                pass = true;
                break;
            }
        }
        if (!pass) {
            Toast.makeText(getContext(), "Can't see this relay", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isChecked) {
            addRelay.add(relay);
        } else {
            addRelay.add(relay);
        }
    }

    private void UploadData() {
        if (mode != 1 && mode != 2) {
            Toast.makeText(getActivity(),"Please select mode", Toast.LENGTH_SHORT).show();
            return;
        }
        if(time.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(),"Please add time", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(),"Please add name", Toast.LENGTH_SHORT).show();
            return;
        }

        for (list_relay relay : mListRelay) {
            boolean found = false;
            Iterator<list_relay> iterator = addRelay.iterator();
            while (iterator.hasNext()) {
                list_relay relay1 = iterator.next();
                if (relay.getRelay_id() == relay1.getRelay_id()) {
                    found = true;
                    if (!relay.isChecked()) {
                        iterator.remove();
                    }
                    break;
                }
            }
            if (relay.isChecked() && !found) {
                addRelay.add(relay);
            }
        }

        if(addRelay.isEmpty())
        {
            Toast.makeText(getActivity(),"Please select relays", Toast.LENGTH_SHORT).show();
            return;
        }

        List<list_relay> send=new ArrayList<>();
        for(list_relay relay:addRelay)
        {
            if(relay.isChecked())
            {
                send.add(relay);
            }
        }

        listAuto.setName(name.getText().toString());
        listAuto.setMode(mode);
        listAuto.setTime(time.getText().toString());
        listAuto.setListRelays(send);

        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listAuto";
        myRef = database.getReference(index);

        myRef.child(String.valueOf(listAuto.getIndex())).updateChildren(listAuto.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                befor_addItemAndReload("Update automation  "+name.getText().toString()+" .");
                getParentFragmentManager().popBackStack();
            }
        });
    }

}