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
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class Profile extends Fragment {

    Button btn_sign_out, btn_update_profile;
    LinearLayout formlogin_none, formlogin_done;
    TextView sign_in, sign_up, edit_email, edit_password;
    EditText edit_nick_name;
    ImageView img_avatar,close_button;
    FirebaseUser user;
    private Uri mUri;
    private View view;
    private static final int MY_REQUEST_CODE = 101;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;
    private DatabaseReference myRef;

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
            getFileDatabase();
            formlogin_none.setVisibility(View.GONE);
            formlogin_done.setVisibility(View.VISIBLE);
            edit_profile();
        }

        return view;
    }

    void edit_profile() {
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(getActivity()).load(photoUrl).error(R.drawable.ic_avatar_default).into(img_avatar);
        edit_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            edit_nick_name.setText(user.getDisplayName());
        }
        if(user.getDisplayName()!=null)
        {
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
                form_signOut();
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
            public void onClick(View v) { form_login();}
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form_register();
            }
        });
    }

    private void form_login(){
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).form_login("profile");
        }
    }

    private void form_register(){
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).form_register("profile");
        }
    }

    private void form_signOut() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).sign_out("profile");
        }
    }

    private void onClickRequestPermission() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mUri=mainActivity.openGallery();
            Glide.with(getActivity()).load(mUri).error(R.drawable.ic_avatar_default).into(img_avatar);
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
           mUri=mainActivity.openGallery();
            Glide.with(getActivity()).load(mUri).error(R.drawable.ic_avatar_default).into(img_avatar);
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
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

    private void onClickUpdateProfile() {
        if (user == null) {
            return;
        }
        String displayName = edit_nick_name.getText().toString().trim();
        if (displayName.contains(" ")) {
            Toast.makeText(getContext(), "Nickname cannot contain spaces", Toast.LENGTH_SHORT).show();
            return;
        }
        if (displayName.isEmpty()) {
            Toast.makeText(getContext(), "Nickname cannot be empty", Toast.LENGTH_SHORT).show();
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
                            user=FirebaseAuth.getInstance().getCurrentUser();
                            String name=user.getDisplayName();
                            openDialogUpdateUser(name);
                            refresh_activity();
                            getFileDatabase();
                            befor_addItemAndReload("Update Profile .");
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
                                                    befor_addItemAndReload("Update Password .");
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
            ((MainActivity) getActivity()).updateUI();
        }
    }

    private void openDialogUpdateUser(String name)
    {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateData(name);
        }
    }


    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        long id = db.addItem(item);
        if (id != -1) {
            loadData();
        }
    }

    private void loadData() {
        adapter = new RecycleViewAdapter();
        List<Item> list = db.getAll();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private void befor_addItemAndReload(String detail) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month= calendar.get(Calendar.MONTH) + 1;
        int year= calendar.get(Calendar.YEAR);
        String shour = String.valueOf(hour);
        String sminute = String.valueOf(minute);
        String sday=String.valueOf(day);
        String smonth=String.valueOf(month);
        String syear=String.valueOf(year);
        if(hour<10)
        {
            shour="0"+shour;
        }
        if(minute<10)
        {
            sminute="0"+sminute;
        }
        String timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
        addItemAndReload(timePicker, detail);
    }

    private void getFileDatabase(){
        if (user != null) {
            String uid = user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference("user_inform").child(uid).child("file");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String databaseName = dataSnapshot.getValue(String.class);
                    if (databaseName != null && !databaseName.isEmpty()) {
                        db = new SQLiteHelper(getContext(), databaseName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


}