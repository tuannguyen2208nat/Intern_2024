package com.example.intern_2024.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.intern_2024.MainActivity;
import com.example.intern_2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;

public class Profile extends Fragment {

    Button btn_sign_out,btn_update_profile;
    LinearLayout formlogin_none, formlogin_done;
    TextView sign_in, sign_up,edit_password;
    EditText edit_full_name,edit_email;
    ImageView img_avatar;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        btn_update_profile=view.findViewById(R.id.btn_update_profile);
        formlogin_none = view.findViewById(R.id.formlogin_none);
        formlogin_done = view.findViewById(R.id.formlogin_done);
        sign_in = view.findViewById(R.id.sign_in);
        sign_up = view.findViewById(R.id.sign_up);
        edit_full_name = view.findViewById(R.id.edit_full_name);
        edit_email = view.findViewById(R.id.edit_email);
        edit_password = view.findViewById(R.id.edit_password);
        img_avatar = view.findViewById(R.id.img_avatar);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            formlogin_none.setVisibility(View.VISIBLE);
            formlogin_done.setVisibility(View.GONE);
            form_login_profile();
        } else {
            user=FirebaseAuth.getInstance().getCurrentUser();
            formlogin_none.setVisibility(View.GONE);
            formlogin_done.setVisibility(View.VISIBLE);
            edit_profile();
        }

        return view;
    }

    void edit_profile() {
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(img_avatar);
        edit_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null)
        {
            edit_full_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
        edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////Dialog box
            }
        });
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                formlogin_none.setVisibility(View.VISIBLE);
                formlogin_done.setVisibility(View.GONE);
                Navigation.findNavController(v).navigate(R.id.menuHome);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateUI(null);
                }
            }
        });
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///Update profile
            }
        });
    }

    void form_login_profile() {
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

    void form_login() {
        Dialog dialog = new Dialog(getActivity());
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
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).updateUI(auth.getCurrentUser());
                                    }
                                    dialog.dismiss();
                                    formlogin_none.setVisibility(View.GONE);
                                    formlogin_done.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getActivity(), "Email or password is incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    void form_register() {
        Dialog dialog = new Dialog(getActivity());
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

                if (password1Str.length() < 8 || password2Str.length() < 8) {
                    Toast.makeText(getActivity(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password1Str.equals(password2Str)) {
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(usernameStr, password1Str)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Registered successfully.", Toast.LENGTH_SHORT).show();
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).updateUI(auth.getCurrentUser());
                                    }
                                    dialog.dismiss();
                                    formlogin_none.setVisibility(View.GONE);
                                    formlogin_done.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });
    }
}
