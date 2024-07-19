package com.example.intern_2024;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.bumptech.glide.Glide;
import com.example.intern_2024.fragment.Accessories;
import com.example.intern_2024.fragment.Automation.Automation;
import com.example.intern_2024.fragment.Home;
import com.example.intern_2024.fragment.Profile;
import com.example.intern_2024.welcome.welcome_form_login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private DatabaseReference myRef;
    TextView name_user, email_user;
    View headerView;
    ImageView image_user, back_login;
    FirebaseUser user;
    Uri imageUri;
    Fragment Home, Accessories, Automation, Profile;
    Fragment active;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

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
        active = Home;

        fm.beginTransaction().add(R.id.navHostFragment, Profile, "4").hide(Profile).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Automation, "3").hide(Automation).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Accessories, "2").hide(Accessories).commit();
        fm.beginTransaction().add(R.id.navHostFragment, Home, "1").commit();

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
                    active = Home;
                    break;
                case R.id.menuAccessories:
                    fm.beginTransaction().hide(active).show(Accessories).commit();
                    active = Accessories;
                    break;
                case R.id.menuAutomation:
                    fm.beginTransaction().hide(active).show(Automation).commit();
                    active = Automation;
                    break;
                case R.id.menuProfile:
                    fm.beginTransaction().hide(active).show(Profile).commit();
                    active = Profile;
                    break;
                default:
                    return false;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

}
