package com.example.intern_2024.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intern_2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class welcome_form_forgot_password extends AppCompatActivity {
    EditText email;
    Button btn_send;
    TextView sign_in,sign_up,done_sign_in,done_sign_up;
    LinearLayout form_none_send,form_done_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_form_forgot_password);

        email = findViewById(R.id.email);
        btn_send = findViewById(R.id.btn_send);
        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.sign_up);
        form_none_send = findViewById(R.id.form_none_send);
        form_done_send = findViewById(R.id.form_done_send);
        done_sign_in = findViewById(R.id.done_sign_in);
        done_sign_up = findViewById(R.id.done_sign_up);

        form_none_send.setVisibility(View.VISIBLE);
        form_done_send.setVisibility(View.GONE);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_login();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_register();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_done();
            }
        });

    }

    private void send_done(){
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(welcome_form_forgot_password.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidEmailFormat(email.getText().toString())) {
            Toast.makeText(welcome_form_forgot_password.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(welcome_form_forgot_password.this, "Email sent.", Toast.LENGTH_SHORT).show();
                            form_none_send.setVisibility(View.GONE);
                            form_done_send.setVisibility(View.VISIBLE);
                            startCountdownTimer();
                        }
                    }
                });

        done_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_login();
            }
        });
        done_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_register();
            }
            });

    }

    private void startCountdownTimer() {
        btn_send.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btn_send.setText("Send (" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                btn_send.setEnabled(true);
                btn_send.setText("Send");
            }
        }.start();
    }


    private boolean isValidEmailFormat(String email) {
        // Regular expression to check the email format xx@abc.xyz
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }


    private void change_to_login(){
        Intent   intent = new Intent(welcome_form_forgot_password.this, welcome_form_login.class);
        startActivity(intent);
        finish();
    }

    private void change_to_register(){
        Intent intent = new Intent(welcome_form_forgot_password.this, welcome_form_register.class);
        startActivity(intent);
        finish();
    }

}