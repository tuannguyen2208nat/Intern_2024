package com.example.intern_2024.fragment.accessories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.intern_2024.R;
import com.example.intern_2024.model.list_auto;

import java.util.ArrayList;

public class List_Automation extends Fragment {
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

        start();

        return view;

    }

    private void start(){

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatatoAutomation();
            }
        });
    }

    private void  updateDatatoAutomation(){
//        list_auto auto=new list_auto();
        sharedViewModel.setEditTextValue(editText.getText().toString());
        getParentFragmentManager().popBackStack();
    }

}
