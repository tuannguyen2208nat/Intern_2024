package com.example.intern_2024.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RelayAdapter;
import com.example.intern_2024.model.list_relay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    FloatingActionButton relay_add;
    ImageView close_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accessories, container, false);
        rcvRelay=view.findViewById(R.id.rcv_relay);
        relay_add=view.findViewById(R.id.relay_add);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvRelay.setLayoutManager(linearLayoutManager);

        mListRelay=new ArrayList<>();
        mRelayAdapter = new RelayAdapter(mListRelay, new RelayAdapter.IClickListener() {
            @Override
            public void onClickupdateRelay(list_relay relay) {
                openDialogupdateRelay(relay);
            }

            @Override
            public void onClickdeleteRelay(list_relay relay) {

            }

            @Override
            public void onClickuseRelay(list_relay relay) {

            }
        });
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

//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(mListRelay!=null)
//                {
//                    mListRelay.clear();
//                }
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    list_relay listRelay = dataSnapshot.getValue(list_relay.class);
//                    if (listRelay != null) {
//                        mListRelay.add(listRelay);
//                    }
//                }
//                mRelayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Get list relay failed", Toast.LENGTH_SHORT).show();
//            }
//        });


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if(listRelay!=null)
                {
                    mListRelay.add(listRelay);
                    mRelayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                list_relay listRelay = snapshot.getValue(list_relay.class);
                if (listRelay == null || mListRelay == null || mListRelay.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListRelay.size(); i++) {
                    if (mListRelay.get(i).getIndex() == listRelay.getIndex()) {
                        mListRelay.get(i).setName(listRelay.getName());
                        mRelayAdapter.notifyItemChanged(i); // Cập nhật chỉ mục cụ thể thay vì toàn bộ danh sách
                        break;
                    }
                }
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openDialogupdateRelay(list_relay list_relay) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_change_name_relay);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listRelay";
        myRef = database.getReference(index);
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

        EditText change_name_device = dialog.findViewById(R.id.change_name_device);
        Button button_name_device = dialog.findViewById(R.id.button_name_device);
        button_name_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = change_name_device.getText().toString();
                list_relay.setName(newName);
                String uid = user.getUid();
                String index = "user_inform/" + uid + "/listRelay/relay" + list_relay.getIndex();
                myRef = database.getReference(index);
                Map<String, Object> relayUpdates = new HashMap<>();
                relayUpdates.put("name", newName);
                myRef.updateChildren(relayUpdates);
                Toast.makeText(getContext(), "Update Relay successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }


}
