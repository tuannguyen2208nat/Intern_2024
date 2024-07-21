package com.example.intern_2024.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intern_2024.MainActivity;
import com.example.intern_2024.R;
import java.util.Locale;

public class Setting extends Fragment {

    private View view;
    private Spinner language_spinner;
    private Spinner theme_spinner;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        language_spinner = view.findViewById(R.id.language_spinner);
        theme_spinner = view.findViewById(R.id.theme_spinner);
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        start();

        return view;
    }

    private void start() {

        boolean isDarkMode = sharedPreferences.getBoolean("Dark_Mode", false);
        theme_spinner.setSelection(isDarkMode ? 1 : 0);

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                if ("English".equals(selectedOption)) {
                    updateLanguage("en");
                    Toast.makeText(getContext(), "English selected", Toast.LENGTH_SHORT).show();
                } else if ("Tiếng Việt".equals(selectedOption)) {
                    updateLanguage("vi");
                    Toast.makeText(getContext(), "Tiếng Việt selected", Toast.LENGTH_SHORT).show();
                }
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
                if ("Light mode".equals(selectedOption)) {
                    updateTheme(false);
                    Toast.makeText(getContext(), "Light mode selected", Toast.LENGTH_SHORT).show();
                } else if ("Dark mode".equals(selectedOption)) {
                    updateTheme(true);
                    Toast.makeText(getContext(), "Dark mode selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateLanguage(String lang) {
        if (lang.isEmpty()) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("App_Language", lang);
        editor.apply();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Resources resources = getResources();
        android.content.res.Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        // Get the new context and apply it
        Context context = getContext().createConfigurationContext(config);

        // Apply the new configuration to the resources
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Refresh the activity to apply language changes
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Optionally finish the current activity
    }

    private void updateTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Dark_Mode", isDarkMode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
