package com.example.intern_2024.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Accessories extends Fragment {
    private View view;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText index,index_relay;
    Button button_relay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accessories, container, false);
        database= FirebaseDatabase.getInstance();
        index_relay=view.findViewById(R.id.index_relay);
        button_relay=view.findViewById(R.id.button_relay);
        button_relay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRelay(Integer.parseInt(index_relay.getText().toString()));
            }
        });

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
}