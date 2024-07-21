package com.example.intern_2024;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.intern_2024.fragment.Accessories;
import com.example.intern_2024.fragment.Automation.Automation;
import com.example.intern_2024.fragment.Home;
import com.example.intern_2024.fragment.Profile;
import com.example.intern_2024.fragment.Setting;
import com.example.intern_2024.welcome.welcome_form_login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private DatabaseReference myRef;
    private TextView textTitle, name_user, email_user;
    private View headerView;
    private ImageView image_user, back_login;
    private FirebaseUser user;
    private Uri imageUri;
    private Fragment Home, Accessories, Automation, Profile, Setting;
    private Fragment active;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("Dark_Mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        String language = sharedPreferences.getString("App_Language", "en");
        setLocale(language);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        textTitle = findViewById(R.id.textTitle);
        headerView = navigationView.getHeaderView(0);
        name_user = headerView.findViewById(R.id.name_user);
        email_user = headerView.findViewById(R.id.email_user);
        image_user = headerView.findViewById(R.id.image_user);
        back_login = headerView.findViewById(R.id.back_login);

        navigationView.setItemIconTintList(null);

        user = FirebaseAuth.getInstance().getCurrentUser();

        findViewById(R.id.menuIcon).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        fm = getSupportFragmentManager();
        Home = new Home();
        Accessories = new Accessories();
        Automation = new Automation();
        Profile = new Profile();
        Setting = new Setting();
        active = Home;

        fm.beginTransaction().add(R.id.navHostFragment, Setting, "5").hide(Setting).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Profile, "4").hide(Profile).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Automation, "3").hide(Automation).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Accessories, "2").hide(Accessories).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Home, "1").commit();
        textTitle.setText("Home");

        back_login.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        updateUI();

        navigationView.setNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void updateUI() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email_user.setText(user.getEmail());
            name_user.setText(user.getDisplayName());
            Glide.with(MainActivity.this)
                    .load(user.getPhotoUrl())
                    .error(R.drawable.ic_avatar_default)
                    .into(image_user);
        }
    }

    public void form_sign_out() {
        Fragment automationFragment = fm.findFragmentByTag("3");
        if (automationFragment instanceof Automation) {
            ((Automation) automationFragment).cancelAllAlarms();
        }
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, welcome_form_login.class);
        startActivity(intent);
        finish();
    }

    private final NavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuHome:
                    fm.beginTransaction().hide(active).show(Home).commit();
                    textTitle.setText(getString(R.string.home));
                    active = Home;
                    break;
                case R.id.menuAccessories:
                    fm.beginTransaction().hide(active).show(Accessories).commit();
                    textTitle.setText(getString(R.string.accessories));
                    active = Accessories;
                    break;
                case R.id.menuAutomation:
                    fm.beginTransaction().hide(active).show(Automation).commit();
                    textTitle.setText(getString(R.string.automation));
                    active = Automation;
                    break;
                case R.id.menuProfile:
                    fm.beginTransaction().hide(active).show(Profile).commit();
                    textTitle.setText(getString(R.string.profile));
                    active = Profile;
                    break;
                case R.id.menuSetting:
                    fm.beginTransaction().hide(active).show(Setting).commit();
                    textTitle.setText(getString(R.string.setting));
                    active = Setting;
                    break;
                default:
                    return false;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Resources resources = getResources();
        android.content.res.Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
