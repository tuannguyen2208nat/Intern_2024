package com.example.intern_2024.fragment.Automation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RelayAutoAdapter;
import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;

import java.util.ArrayList;
import java.util.List;

public class List_Automation extends Fragment {
    private list_auto listAuto;
    private RecyclerView rcvRelay;
    private RelayAutoAdapter mRelayAdapter;
    private List<list_relay> mListRelay, addRelay;
    private SharedViewModel sharedViewModel;
    private View view;
    private ImageView backIcon;
    private Button create;
    private EditText name_auto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_automation, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        backIcon = view.findViewById(R.id.backIcon);
        create = view.findViewById(R.id.create);
        rcvRelay = view.findViewById(R.id.rcvRelay);
        name_auto = view.findViewById(R.id.name_auto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        mListRelay = new ArrayList<>();
        addRelay = new ArrayList<>();
        listAuto = new list_auto();

        sharedViewModel.GetListRelay().observe(getViewLifecycleOwner(), new Observer<ArrayList<list_relay>>() {
            @Override
            public void onChanged(ArrayList<list_relay> listRelays) {
                if (listRelays != null && !listRelays.isEmpty()) {
                    mListRelay = listRelays;
                    listAuto.setListRelays(mListRelay);
                    mRelayAdapter.updateRelayAuto(mListRelay);
                }
            }
        });



        return view;
    }

    private void start() {

        mRelayAdapter = new RelayAutoAdapter(mListRelay, new RelayAutoAdapter.IClickListener() {
            @Override
            public void onClickSelectRelay(list_relay relay, boolean isChecked) {
                handleRelaySelection(relay, isChecked);
            }
        });
        rcvRelay.setAdapter(mRelayAdapter);

        setListeners();

    }

    private void setListeners() {
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendListAuto();
            }
        });
    }

    private void handleRelaySelection(list_relay relay, boolean isChecked) {
        boolean pass = false;
        for (list_relay item : mListRelay) {
            if (item.getRelay_id()==(relay.getRelay_id())) {
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

    private void sendListAuto() {
        if (listAuto.getSize() == 0) {
            Toast.makeText(getContext(), "No relays selected", Toast.LENGTH_SHORT).show();
            return;
        }
        listAuto.setName(name_auto.getText().toString());
        listAuto.waybackvalue();
        sharedViewModel.SetListAuto(listAuto);
        getParentFragmentManager().popBackStack();
    }
}
