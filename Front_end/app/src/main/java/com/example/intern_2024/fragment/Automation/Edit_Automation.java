package com.example.intern_2024.fragment.Automation;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RelayAutoAdapter;
import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;

import java.util.ArrayList;
import java.util.List;

public class Edit_Automation extends Fragment {
    private View view;
    private list_auto listAuto;
    private RecyclerView rcvRelay;
    private RelayAutoAdapter mRelayAdapter;
    private List<list_relay> mListRelay;
    private ImageView backIcon;
    private Button save;
    private EditText name_auto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.edit_automation, container, false);


        backIcon = view.findViewById(R.id.backIcon);
        save = view.findViewById(R.id.save);
        rcvRelay = view.findViewById(R.id.rcvRelay);
        name_auto = view.findViewById(R.id.name_auto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        mListRelay = new ArrayList<>();
        listAuto = new list_auto();
        mRelayAdapter = new RelayAutoAdapter(mListRelay, new RelayAutoAdapter.IClickListener() {
            @Override
            public void onClickSelectRelay(list_relay relay, boolean isChecked) {
//                handleRelaySelection(relay, isChecked);
            }
        });
        rcvRelay.setAdapter(mRelayAdapter);

        setListeners();


        return view;
    }



    private void updateRelays() {
        for (list_relay relayA : mListRelay) {
            for (list_relay relayB : listAuto.getListRelays()) {
                if (relayA.getRelay_id() == relayB.getRelay_id()) {
//                    relayA.setChecked(true);
                }
            }
        }
        mRelayAdapter.updateRelayAuto(mListRelay);
    }


    private void setListeners() {
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendListAuto();
            }
        });
    }
}