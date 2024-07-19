package com.example.intern_2024.fragment.Automation;

import android.app.AlertDialog;
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

        mAutoAdapter = new AutoAdapter(mListAuto, new AutoAdapter.IClickListener() {
            @Override
            public void onClickEditAuto(list_auto auto) {
                openDialogEditAuto(auto);
            }

            @Override
            public void onClickUseAuto(list_auto auto, State state) {

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
        bundle.putSerializable("list_auto", auto);

        // Đặt bundle cho fragment

        Edit_Automation fragmentB = new Edit_Automation();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.drawerLayout, fragmentB);
        fragmentTransaction.addToBackStack(null);
        fragmentB.setArguments(bundle);
        fragmentTransaction.commit();
    }

}