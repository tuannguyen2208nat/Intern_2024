package com.example.intern_2024.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.intern_2024.R;
import com.example.intern_2024.database.SQLiteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends Fragment {
    private DatabaseReference myRef;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Assuming you have already initialized Firebase and have a reference to the current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference("user_inform").child(uid).child("file");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String databaseName = dataSnapshot.getValue(String.class);
                    if (databaseName != null && !databaseName.isEmpty()) {
//                        SQLiteHelper dbHelper = new SQLiteHelper(getContext(), databaseName);
//                        // Now you can use dbHelper to interact with the database
                        Toast.makeText(getContext(), "Database name: " + databaseName, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the case where the database name is not available
                        Toast.makeText(getContext(), "Database name not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(getContext(), "Failed to get database name: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }
}

