package com.example.intern_2024.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intern_2024.MainActivity;
import com.example.intern_2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class welcome_app extends AppCompatActivity {
    final int SPLASH_TIME_OUT = 5000;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_app);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            change_to_home();
        } else {
            change_to_login();
        }
    }

    private void change_to_home(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent   intent = new Intent(welcome_app.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void change_to_login(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent   intent = new Intent(welcome_app.this, welcome_form_login.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
