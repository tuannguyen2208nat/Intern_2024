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
import com.example.intern_2024.adapter.RelayAdapter;
import com.example.intern_2024.model.list_auto;
import com.example.intern_2024.model.list_relay;
import com.nightonke.jellytogglebutton.State;

import java.util.ArrayList;
import java.util.List;

public class List_Automation extends Fragment {
    private RecyclerView rcvRelay;
    private RelayAdapter mRelayAdapter;
    public List<list_relay> mListRelay;
    private SharedViewModel sharedViewModel;
    private View view;
    private EditText editText;
    private ImageView backIcon;
    Button create;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_automation, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        backIcon=view.findViewById(R.id.backIcon);
        editText=view.findViewById(R.id.editText);
        create=view.findViewById(R.id.create);

        rcvRelay = view.findViewById(R.id.rcvRelay);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);
        mListRelay=new ArrayList<>();
        mRelayAdapter = new RelayAdapter(mListRelay);
        rcvRelay.setAdapter(mRelayAdapter);
        sharedViewModel.GetListRelay().observe(getViewLifecycleOwner(), new Observer<ArrayList<list_relay>>() {
            @Override
            public void onChanged(ArrayList<list_relay> listRelays) {
                if (listRelays != null && !listRelays.isEmpty()) {
                    mListRelay = listRelays;
                    mRelayAdapter.updateRelayList(mListRelay);
                    mRelayAdapter.notifyDataSetChanged();
                }
            }
        });

        start();

        return view;

    }

    private void start(){

        rcvRelay.setAdapter(mRelayAdapter);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendlistAuto();
            }
        });
    }

    private void  SendlistAuto(){
        list_auto listAuto=new list_auto();
        sharedViewModel.SetListAuto(listAuto);
        getParentFragmentManager().popBackStack();
    }

}
