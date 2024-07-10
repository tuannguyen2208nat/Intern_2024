package com.example.intern_2024;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.bumptech.glide.Glide;
import com.example.intern_2024.fragment.Home;
import com.example.intern_2024.fragment.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CODE=10;
    TextView sign_in, sign_up;
    TextView name_user, email_user;
    ConstraintLayout constraintLayout_1, constraintLayout_2;
    View headerView;
    ImageView image_user, back_login;
    FirebaseUser user;
    private final Profile mProfile=new Profile();
    final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK ) {
                        Intent intent=result.getData();
                        if(intent==null){
                            return;
                        }
                        Uri uri=intent.getData();
                        mProfile.setmUri(uri);
                        try {
                            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            mProfile.setBitmapImageView(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        TextView textTitle = findViewById(R.id.textTitle);
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);

        headerView = navigationView.getHeaderView(0);
        constraintLayout_1 = headerView.findViewById(R.id.formlogin_none);
        constraintLayout_2 = headerView.findViewById(R.id.formlogin_done);
        sign_in = headerView.findViewById(R.id.sign_in);
        sign_up = headerView.findViewById(R.id.sign_up);

        name_user = headerView.findViewById(R.id.name_user);
        email_user = headerView.findViewById(R.id.email_user);
        image_user = headerView.findViewById(R.id.image_user);

        back_login = headerView.findViewById(R.id.back_login);

        navigationView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(navigationView, navController);

        user = FirebaseAuth.getInstance().getCurrentUser();

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

        back_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        updateUI(user);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form_login();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form_register();
            }
        });
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            constraintLayout_1.setVisibility(View.GONE);
            constraintLayout_2.setVisibility(View.VISIBLE);
            email_user.setText(user.getEmail());
            if (user.getDisplayName() == null) {
                name_user.setVisibility(View.GONE);
            } else {
                name_user.setText(user.getDisplayName());
            }
            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(image_user);
        } else {
            constraintLayout_1.setVisibility(View.VISIBLE);
            constraintLayout_2.setVisibility(View.GONE);
        }
    }

    void form_login() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_form_login);
        dialog.show();
        EditText username = dialog.findViewById(R.id.username);
        EditText password = dialog.findViewById(R.id.password);
        TextView forgot_password_text = dialog.findViewById(R.id.forgot_password_text);
        TextView signup_text = dialog.findViewById(R.id.signup_text);

        forgot_password_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add forgot password functionality here
            }
        });

        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                form_register();
            }
        });

        Button button = dialog.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(usernameStr, passwordStr)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    user = auth.getCurrentUser();
                                    dialog.dismiss();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(MainActivity.this, "Email or password is incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    void form_register() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_form_register);
        dialog.show();

        EditText username = dialog.findViewById(R.id.username);
        EditText password_1 = dialog.findViewById(R.id.password_1);
        EditText password_2 = dialog.findViewById(R.id.password_2);
        Button registerButton = dialog.findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String password1Str = password_1.getText().toString();
                String password2Str = password_2.getText().toString();

                TextView signin_text=dialog.findViewById(R.id.signin_text);

                signin_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        form_login();
                    }
                });

                if (password1Str.length() < 8 || password2Str.length() < 8) {
                    Toast.makeText(MainActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password1Str.equals(password2Str)) {
                    Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(usernameStr, password1Str)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    user = auth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(MainActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    public void openGallery(){
            Intent intent =new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
        if(requestCode==MY_REQUEST_CODE)
        {
            if(grantResult.length>0 && grantResult[0]== PackageManager.PERMISSION_GRANTED)
            {
                openGallery();
            }
        }
    }
}
