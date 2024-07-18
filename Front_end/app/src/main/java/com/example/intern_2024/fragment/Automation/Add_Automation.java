package com.example.intern_2024.fragment.Automation;

import android.app.AlertDialog;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.adapter.RelayAutoAdapter;
import com.example.intern_2024.database.MQTTHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add_Automation extends Fragment {
    RecycleViewAdapter adapter;
    private View view;
    private RecyclerView rcvRelay;
    private RelayAutoAdapter mRelayAdapter;
    private List<list_relay> mListRelay, addRelay;
    private ImageView backIcon;
    private Button create;
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

        view = inflater.inflate(R.layout.add_automation, container, false);

        backIcon = view.findViewById(R.id.backIcon);
        create = view.findViewById(R.id.create);
        rcvRelay = view.findViewById(R.id.rcvRelay);
        name = view.findViewById(R.id.name);
        select_mode= view.findViewById(R.id.mode);
        time=view.findViewById(R.id.time);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mListRelay = new ArrayList<>();
        addRelay = new ArrayList<>();
        listAuto = new list_auto();

        setupRelayAdapter();
        start();

        return view;
    }

    private void start() {
        getFileDatabase();

        String[] options = { " ","On","Off"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_mode.setAdapter(adapter);
        select_mode.setSelection(0);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAuto();
            }
        });

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

        if (user != null) {
            getlistRelay();
        }
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
            addRelay.remove(relay);
        }
        listAuto.setListRelays(addRelay);
    }

    private void CreateAuto() {
        if (listAuto.getSize() == 0) {
            Toast.makeText(getContext(), "No relays selected", Toast.LENGTH_SHORT).show();
            return;
        }
        listAuto.setName(name.getText().toString());
        UploadData();
    }

    private void UploadData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        if (mode != 1 && mode != 2) {
            Toast.makeText(getActivity(),"Please select mode", Toast.LENGTH_SHORT).show();
            return;
        }
        if(time.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(),"Please add time", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listAuto";
        myRef = database.getReference(index);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    list_auto auto = child.getValue(list_auto.class);
                    if (auto != null && auto.getIndex() > maxId) {
                        maxId = auto.getIndex();
                    }
                }
                int newId = maxId + 1;
                listAuto.setIndex(newId);
                listAuto.setMode(mode);
                listAuto.setTime(time.getText().toString());

                myRef.child(String.valueOf(newId)).setValue(listAuto, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        befor_addItemAndReload("Add automation  " + listAuto.getName() + " .");
                        Toast.makeText(getActivity(), "Add automation successfully", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
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
                    mListRelay.add(listRelay);
                    mRelayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay == null || mListRelay.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListRelay.size(); i++) {
                    if (listRelay.getIndex() == mListRelay.get(i).getIndex()) {
                        mListRelay.set(i, listRelay);
                        mRelayAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay == null || mListRelay.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListRelay.size(); i++) {
                    if (listRelay.getIndex() == mListRelay.get(i).getIndex()) {
                        mListRelay.remove(i);
                        mRelayAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
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

    private void showAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Attention")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}