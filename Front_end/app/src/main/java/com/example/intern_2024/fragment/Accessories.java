package com.example.intern_2024.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RelayAdapter;
import com.example.intern_2024.model.list_relay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accessories extends Fragment {
    private View view;
    private RecyclerView rcvRelay;
    private RelayAdapter mRelayAdapter;
    private List<list_relay> mListRelay;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accessories, container, false);
        rcvRelay=view.findViewById(R.id.rcv_relay);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        mListRelay=new ArrayList<>();
        mRelayAdapter = new RelayAdapter(mListRelay);
        rcvRelay.setAdapter(mRelayAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance();

        if (user != null) {
            getlistRelay();
        }
        return view;
    }

    public void deleteRelay(int id){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        myRef = database.getReference("user_inform");
        Map<String,Object> map = new HashMap<>();
        String index=uid+"/listRelay/"+"relay"+id;
        map.put(index,-1);
        myRef.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(), "Delete Relay_"+id+" successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getlistRelay() {
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listRelay";
        myRef = database.getReference(index);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListRelay.clear(); // Clear the list before adding new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                    if (listRelay != null) {
                        mListRelay.add(listRelay);
                    }
                }
                mRelayAdapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Get list relay failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
