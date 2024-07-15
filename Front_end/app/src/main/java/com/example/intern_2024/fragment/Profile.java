package com.example.intern_2024.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.intern_2024.MainActivity;
import com.example.intern_2024.R;
import com.example.intern_2024.adapter.RecycleViewAdapter;
import com.example.intern_2024.database.SQLiteHelper;
import com.example.intern_2024.model.Item;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class Profile extends Fragment {

    Button btn_sign_out, btn_update_profile;
    TextView edit_email, edit_password;
    EditText edit_nick_name;
    ImageView img_avatar,close_button;
    FirebaseUser user;
    Uri mUri;
    private View view;
    private static final int MY_REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;
    private DatabaseReference myRef;
    StorageReference storageRef ;
    FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        btn_update_profile = view.findViewById(R.id.btn_update_profile);
        edit_nick_name = view.findViewById(R.id.edit_nick_name);
        edit_email = view.findViewById(R.id.edit_email);
        edit_password = view.findViewById(R.id.edit_password);
        img_avatar = view.findViewById(R.id.img_avatar);

        user=FirebaseAuth.getInstance().getCurrentUser();
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReferenceFromUrl("gs://intern-2024-7b2c9.appspot.com");

        getFileDatabase();
        edit_profile();

        return view;
    }

    void edit_profile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(getActivity()).load(photoUrl).error(R.drawable.ic_avatar_default).into(img_avatar);
        edit_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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

    private void form_signOut() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).form_sign_out("profile");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
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

        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName);

        if (mUri != null) {
            profileUpdatesBuilder.setPhotoUri(mUri);
        }

        UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_SHORT).show();
                            user=FirebaseAuth.getInstance().getCurrentUser();
                            befor_addItemAndReload("Update Profile .");
                            if(mUri!=null)
                            {
                                uploadImageToFirebaseStorage(mUri);
                            }
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


    ///////Image///////
    private void onClickRequestPermission() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Toast.makeText(getActivity(), "Image selected successfully", Toast.LENGTH_SHORT).show();
            mUri = data.getData();
            Glide.with(getActivity()).load(mUri).error(R.drawable.ic_avatar_default).into(img_avatar);
        }
        else {
            Toast.makeText(getActivity(), "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImageToFirebaseStorage(Uri mUri) {
        StorageReference mountainsRef = storageRef.child(user.getUid()+"/avatar/"+mUri.getLastPathSegment());
        UploadTask uploadTask = mountainsRef.putFile(mUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mountainsRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

            }
        });
    }
    /////////////////

    ///////SQLite///////
    private void getFileDatabase(){
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
    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        long id = db.addItem(item);
        if (id != -1) {
            adapter = new RecycleViewAdapter();
            List<Item> list = db.getAll();
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
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

    /////////////////


}