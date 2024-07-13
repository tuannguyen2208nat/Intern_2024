package com.example.intern_2024.fragment;

import android.animation.Animator;
import android.app.AlertDialog;
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
import com.google.firebase.database.Query;
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


        user = FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance();

        mRelayAdapter = new RelayAdapter(mListRelay, new RelayAdapter.IClickListener() {
            @Override
            public void onClickupdateRelay(list_relay relay) {
                openDialogUpdateRelay(relay);
            }

            @Override
            public void onClickdeleteRelay(list_relay relay) {deleteRelay(relay);}

            @Override
            public void onClickuseRelay(list_relay relay) {

            }
        });
        rcvRelay.setAdapter(mRelayAdapter);


        relay_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAddRelay();
            }
        });

        if (user != null) {
            getlistRelay();
        }


        return view;
    }

    private void getlistRelay() {
        String uid = user.getUid();
        String index = "user_inform/" + uid + "/listRelay";
        myRef = database.getReference(index);
        Query query=myRef.orderByChild("index");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                list_relay listRelay = dataSnapshot.getValue(list_relay.class);
                if (listRelay != null ) {
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
                    if (listRelay.getIndex() == mListRelay.get(i).getIndex()) {
                       mListRelay.set(i, listRelay);
                        break;
                    }
                }
                mRelayAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                list_relay listRelay = snapshot.getValue(list_relay.class);
                if (listRelay == null || mListRelay == null || mListRelay.isEmpty()) {
                    return;
                }
                for (int i = 0; i < mListRelay.size(); i++) {
                    if (listRelay.getIndex() == mListRelay.get(i).getIndex()) {
                        mListRelay.remove(mListRelay.get(i));
                        break;
                    }
                }
                mRelayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openDialogUpdateRelay(list_relay list_relay) {
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
                myRef.child(String.valueOf(list_relay.getIndex())).updateChildren(list_relay.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getContext(), "Update relay successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void deleteRelay(list_relay list_relay){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_delete_relay);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

        Button button_no=dialog.findViewById(R.id.button_no);
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button button_yes=dialog.findViewById(R.id.button_yes);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = user.getUid();
                String index = "user_inform/" + uid + "/listRelay" ;
                myRef = database.getReference(index);
                myRef.child(String.valueOf(list_relay.getIndex())).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getContext(), "Delete relay successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
    }

    private void openDialogAddRelay(){
        EditText relay_id,set_name_device;
        Button button_add;

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_add_relay);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

        relay_id=dialog.findViewById(R.id.relay_id);
        set_name_device=dialog.findViewById(R.id.set_name_device);
        button_add=dialog.findViewById(R.id.button_add);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relay_id.getText().toString().isEmpty() ) {
                    showAlert("Please enter relay id");
                    return;
                }

                int relay_id_int = Integer.parseInt(relay_id.getText().toString());
                if ( relay_id_int < 1) {
                    showAlert("Relay id must be greater than 0");
                    return;
                }
                if ( relay_id_int > 32) {
                    showAlert("Currently there are only 32 relays");
                    return;
                }
                String set_name_device_str = set_name_device.getText().toString().isEmpty() ?
                        "Relay_" + relay_id_int : set_name_device.getText().toString();

                String uid = user.getUid();
                String index = "user_inform/" + uid + "/listRelay";
                myRef = database.getReference(index);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int maxId = 0;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            list_relay relay = child.getValue(list_relay.class);
                            if(relay_id_int==relay.getRelay_id())
                            {
                                showAlert("Relay id already exists");
                                return;
                            }
                            if (relay != null && relay.getIndex() > maxId) {
                                maxId = relay.getIndex();
                            }
                        }
                        int newId = maxId + 1;
                        list_relay newRelay = new list_relay(newId, relay_id_int, set_name_device_str);
                        myRef.child(String.valueOf(newId) ).setValue(newRelay, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getContext(), "Add relay successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showAlert("Failed to read data: " + error.getMessage());
                    }
                });

            }
        });

    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Attention")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
