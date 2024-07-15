package com.example.intern_2024.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern_2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class welcome_form_register extends AppCompatActivity {
    EditText email,password,confirm_password;
    Button btn_sign_up;
    TextView forgot_password,sign_in;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_form_register);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);
        btn_sign_up=findViewById(R.id.btn_sign_up);
        forgot_password=findViewById(R.id.forgot_password);
        sign_in=findViewById(R.id.sign_in);
        database = FirebaseDatabase.getInstance();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_login();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_forgot_password();}
        });


        btn_sign_up.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String emailStr = email.getText().toString();
            String password1Str = password.getText().toString();
            String password2Str = confirm_password.getText().toString();
            String[] parts = emailStr.split("@");
            String name=parts[0];

            if (emailStr.isEmpty() || password1Str.isEmpty() || password2Str.isEmpty()) {
                Toast.makeText(welcome_form_register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmailFormat(emailStr)) {
                Toast.makeText(welcome_form_register.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }


            if (password1Str.length() < 8 || password2Str.length() < 8) {
                Toast.makeText(welcome_form_register.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password1Str.equals(password2Str)) {
                Toast.makeText(welcome_form_register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(emailStr, password1Str)
                    .addOnCompleteListener(welcome_form_register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user=auth.getCurrentUser();
                                updateData(name);
                                UploadData();
                            } else {
                                Toast.makeText(welcome_form_register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
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

private void updateData(String displayName){
    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build();

    user.updateProfile(profileUpdates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user=FirebaseAuth.getInstance().getCurrentUser();
                        change_to_home();
                    } else {
                        return;
                    }
                }
            });
}

    public void UploadData()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
        {
            return;
        }
        String name = "";
        myRef = database.getReference("user_inform");
        String uid = user.getUid();
        String email = user.getEmail();
        String[] parts = email.split("@");
        if (user.getDisplayName() != null) {
            name = user.getDisplayName();
        }
        else
        {
            name=parts[0];
        }
        String filename = parts[0] + ".db";

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("file", filename);
        myRef.child(uid).updateChildren(userMap);
        user= FirebaseAuth.getInstance().getCurrentUser();
    }


private void change_to_home(){
    Intent intent = new Intent(welcome_form_register.this, welcome_login.class);
    intent.putExtra("name", user.getDisplayName());
    startActivity(intent);
    finish();
}

private void change_to_login(){
    Intent   intent = new Intent(welcome_form_register.this, welcome_form_login.class);
    startActivity(intent);
    finish();
}

private void change_to_forgot_password(){
    Intent   intent = new Intent(welcome_form_register.this, welcome_form_forgot_password.class);
    startActivity(intent);
    finish();
}

}