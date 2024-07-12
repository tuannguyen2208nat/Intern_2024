package com.example.intern_2024.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {

    Button btn_sign_out, btn_update_profile;
    LinearLayout formlogin_none, formlogin_done;
    TextView sign_in, sign_up, edit_email, edit_password;
    EditText edit_nick_name;
    ImageView img_avatar,close_button;
    FirebaseUser user;
    FirebaseFirestore db;
    private Uri mUri;
    private View view;
    private static final int MY_REQUEST_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        formlogin_done = view.findViewById(R.id.formlogin_done);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        btn_update_profile = view.findViewById(R.id.btn_update_profile);
        edit_nick_name = view.findViewById(R.id.edit_nick_name);
        edit_email = view.findViewById(R.id.edit_email);
        edit_password = view.findViewById(R.id.edit_password);
        img_avatar = view.findViewById(R.id.img_avatar);

        formlogin_none = view.findViewById(R.id.formlogin_none);
        sign_in = view.findViewById(R.id.sign_in);
        sign_up = view.findViewById(R.id.sign_up);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            formlogin_none.setVisibility(View.VISIBLE);
            formlogin_done.setVisibility(View.GONE);
            form_login_profile();
        } else {
            user = FirebaseAuth.getInstance().getCurrentUser();
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
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            edit_nick_name.setText(user.getDisplayName());
        }

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });
        edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEditPassword();
            }
        });
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                formlogin_none.setVisibility(View.VISIBLE);
                formlogin_done.setVisibility(View.GONE);
                Navigation.findNavController(view).navigate(R.id.menuProfile);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateUI(null);
                }
            }
        });
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
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
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_form_login);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        EditText username = dialog.findViewById(R.id.username);
        EditText password = dialog.findViewById(R.id.password);
        TextView forgot_password_text = dialog.findViewById(R.id.forgot_password_text);
        TextView signup_text = dialog.findViewById(R.id.signup_text);
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

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
                                    NavController navController = Navigation.findNavController(view);
                                    navController.navigate(R.id.menuProfile);
                                    user = auth.getCurrentUser();
                                    refresh_activity();
                                    formlogin_none.setVisibility(View.GONE);
                                    formlogin_done.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
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
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_form_register);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        EditText username = dialog.findViewById(R.id.username);
        EditText password_1 = dialog.findViewById(R.id.password_1);
        EditText password_2 = dialog.findViewById(R.id.password_2);
        Button registerButton = dialog.findViewById(R.id.register_button);
        TextView signin_text = dialog.findViewById(R.id.signin_text);
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

        signin_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                form_login();
            }
        });

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
                if (!isValidEmailFormat(usernameStr)) {
                    Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(usernameStr, password1Str)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Registered successfully.", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(view);
                                    navController.navigate(R.id.menuProfile);
                                    user = auth.getCurrentUser();
                                    refresh_activity();
                                    formlogin_none.setVisibility(View.GONE);
                                    formlogin_done.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Your email has been registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void onClickRequestPermission() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mainActivity.openGallery();
            return;
        }
        if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mainActivity.openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requireActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with gallery opening
                ((MainActivity) requireActivity()).openGallery();
            } else {
                // Permission denied, show a message or handle gracefully
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setBitmapImageView(Bitmap bitmapImageView) {
        img_avatar.setImageBitmap(bitmapImageView);
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    private void onClickUpdateProfile() {
        if (user == null) {
            return;
        }
        String displayName = edit_nick_name.getText().toString().trim();
        if (displayName.contains(" ")) {
            // Show toast message
            Toast.makeText(getContext(), "Nickname cannot contain spaces", Toast.LENGTH_SHORT).show();
            return;
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_SHORT).show();
                            refresh_activity();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onClickEditPassword() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_form_edit_password);
        Window window=dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        EditText current_password = dialog.findViewById(R.id.current_password);
        EditText password_1 = dialog.findViewById(R.id.password_1);
        EditText password_2 = dialog.findViewById(R.id.password_2);
        String email = edit_email.getText().toString().trim();
        close_button=dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();}
        });

        Button change_button = dialog.findViewById(R.id.change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), current_password.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

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
                                user.updatePassword(password1Str)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Update Password Success", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    refresh_activity();
                                                } else {
                                                    Toast.makeText(getActivity(), "Update Password Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
            }
        });
    }

    private void refresh_activity() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateUI(user);
        }
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Attention")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private boolean isValidEmailFormat(String email) {
        // Regular expression to check the email format xx@abc.xyz
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

}