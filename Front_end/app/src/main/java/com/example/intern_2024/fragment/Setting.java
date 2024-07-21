package com.example.intern_2024.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.intern_2024.MainActivity;
import com.example.intern_2024.R;
import com.example.intern_2024.about_my_app.About_my_app;
import com.example.intern_2024.fragment.Automation.Add_Automation;

import java.util.Locale;

public class Setting extends Fragment {

    private View view;
    private Spinner language_spinner;
    private Spinner theme_spinner;
    private SharedPreferences sharedPreferences;
    private Button save_setting;
    private String language;
    private boolean isDarkMode;
    private TextView about_app;

    private static final String PREFS_NAME = "Settings";
    private static final String KEY_LANGUAGE = "App_Language";
    private static final String KEY_DARK_MODE = "Dark_Mode";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        language_spinner = view.findViewById(R.id.language_spinner);
        theme_spinner = view.findViewById(R.id.theme_spinner);
        save_setting = view.findViewById(R.id.save_setting);
        about_app=view.findViewById(R.id.about_app);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        start();
        return view;
    }

    private void start() {
        String currentLanguage = sharedPreferences.getString(KEY_LANGUAGE, "en");
        language = currentLanguage;
        language_spinner.setSelection("en".equals(currentLanguage) ? 0 : 1);

        isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        theme_spinner.setSelection(isDarkMode ? 1 : 0);

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                language = "English".equals(selectedOption) ? "en" : "vi";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        theme_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                isDarkMode = "Dark mode".equals(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        about_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                About_my_app fragmentB = new About_my_app();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.drawerLayout, fragmentB);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        save_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLanguage(language);
                updateTheme(isDarkMode);
                refresh();
            }
        });
    }

    private void updateLanguage(String lang) {
        if (lang.isEmpty()) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void updateTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void refresh() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
