package com.example.intern_2024;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    boolean check = false;
    TextView sign_in,sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);

        findViewById(R.id.menuIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView textTitle=findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });

        View headerView = navigationView.getHeaderView(0);

        ConstraintLayout constraintLayout_1 = headerView.findViewById(R.id.formlogin_none);
        ConstraintLayout constraintLayout_2 = headerView.findViewById(R.id.formlogin_done);
        sign_in=headerView.findViewById(R.id.sign_in);
        sign_up=headerView.findViewById(R.id.sign_up);

            if (check) {
                constraintLayout_1.setVisibility(View.GONE);
                constraintLayout_2.setVisibility(View.VISIBLE);
            } else {
                constraintLayout_1.setVisibility(View.VISIBLE);
                constraintLayout_2.setVisibility(View.GONE);
                sign_in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "sign in", Toast.LENGTH_SHORT).show();
                        form_login();
                    }
                });
                sign_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "sign up", Toast.LENGTH_SHORT).show();}
                });
            }
    }

    void form_login() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_form_login);
        dialog.show();

        // Find the button within the dialog's view hierarchy
        Button button = dialog.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}