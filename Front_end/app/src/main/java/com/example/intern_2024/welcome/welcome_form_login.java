package com.example.intern_2024.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern_2024.MainActivity;
import com.example.intern_2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class welcome_form_login extends AppCompatActivity {

    FirebaseUser user;
    EditText email,password;
    TextView forgot_password,sign_up;
    Button btn_sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_form_login);
        user = FirebaseAuth.getInstance().getCurrentUser();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        forgot_password = findViewById(R.id.forgot_password);
        sign_up = findViewById(R.id.sign_up );

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_register();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_forgot_password();}
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                if (usernameStr.isEmpty()) {
                    Toast.makeText(welcome_form_login.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordStr.isEmpty()) {
                    Toast.makeText(welcome_form_login.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmailFormat(usernameStr)) {
                    Toast.makeText(welcome_form_login.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(usernameStr, passwordStr)
                        .addOnCompleteListener(welcome_form_login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user = auth.getCurrentUser();
                                    change_to_home();
                                } else {
                                    Toast.makeText(welcome_form_login.this, "Email or password is incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private boolean isValidEmailFormat(String email) {
        // Regular expression to check the email format xx@abc.xyz
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    private void change_to_home(){
        Intent intent = new Intent(welcome_form_login.this, welcome_login.class);
        intent.putExtra("name", user.getDisplayName());
        startActivity(intent);
        finish();
    }


    private void change_to_register(){
        Intent   intent = new Intent(welcome_form_login.this, welcome_form_register.class);
        startActivity(intent);
        finish();
    }

    private void change_to_forgot_password(){
        Intent   intent = new Intent(welcome_form_login.this, welcome_form_forgot_password.class);
        startActivity(intent);
        finish();
    }


}