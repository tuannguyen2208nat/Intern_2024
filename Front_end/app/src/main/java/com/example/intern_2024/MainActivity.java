package com.example.intern_2024;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.intern_2024.databinding.ActivityMainBinding;
import com.example.intern_2024.fragment.Accessories;
import com.example.intern_2024.fragment.Automation;
import com.example.intern_2024.fragment.Home;
import com.example.intern_2024.fragment.Profile;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Home(), "HOME");

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                replaceFragment(new Home(), "HOME");
            } else if (item.getItemId() == R.id.nav_accessories) {
                replaceFragment(new Accessories(), "Accessories");
            } else if (item.getItemId() == R.id.nav_automation) {
                replaceFragment(new Automation(), "Automation");
            } else if (item.getItemId() == R.id.nav_profile) {
                replaceFragment(new Profile(), "Profile");
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, fragment, tag);
        fragmentTransaction.commit();
    }
}