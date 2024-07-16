package com.example.intern_2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.intern_2024.asset.ImageLoadTask;
import com.example.intern_2024.welcome.welcome_form_login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    TextView name_user, email_user;
    View headerView;
    ImageView image_user, back_login;
    FirebaseUser user;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        TextView textTitle = findViewById(R.id.textTitle);
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);

        headerView = navigationView.getHeaderView(0);

        name_user = headerView.findViewById(R.id.name_user);
        email_user = headerView.findViewById(R.id.email_user);
        image_user = headerView.findViewById(R.id.image_user);

        back_login = headerView.findViewById(R.id.back_login);

        navigationView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(navigationView, navController);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

//
        findViewById(R.id.menuIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });

        if (savedInstanceState == null) {
            // Initial fragment transaction or setup
            navController.navigate(R.id.menuHome);
        }

        back_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        updateUI();
    }


    public void updateUI() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        email_user.setText(user.getEmail());
        name_user.setText(user.getDisplayName());
        Glide.with(MainActivity.this).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(image_user);
    }

    public void form_sign_out(String fragment)
    {
        FirebaseAuth.getInstance().signOut();
        Intent  intent = new Intent(MainActivity.this, welcome_form_login.class);
        startActivity(intent);
        finish();
    }



}