package com.example.intern_2024.about_my_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.intern_2024.R;


public class About_my_app extends Fragment {

    private View view;
    ImageView backIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_about_my_app, container, false);
        backIcon=view.findViewById(R.id.backIcon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
}