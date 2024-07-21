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
import com.example.intern_2024.asset.ImageLoadTask;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {

    private View view;
    private static final int MY_REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    Button btn_sign_out;
    TextView edit_name,edit_email, edit_password;
    ImageView edit_avatar,close_button,arrow_left_name,arrow_left_email,arrow_left_password,change_avatar;
    FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    StorageReference storageRef ;
    FirebaseStorage storage;
    Uri mUri;
    private SQLiteHelper db;
    private String URL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        edit_name = view.findViewById(R.id.edit_name);
        edit_email = view.findViewById(R.id.edit_email);
        edit_password=view.findViewById(R.id.edit_password);
        edit_avatar = view.findViewById(R.id.edit_avatar);
        arrow_left_name=view.findViewById(R.id.arrow_left_name);
        arrow_left_email=view.findViewById(R.id.arrow_left_email);
        arrow_left_password=view.findViewById(R.id.arrow_left_password);

        user=FirebaseAuth.getInstance().getCurrentUser();
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReferenceFromUrl("gs://intern-2024-7b2c9.appspot.com");

        start();

        return view;
    }

    private void start() {
        getFileDatabase();
        edit_profile();
    }

    void edit_profile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();
        getImagefromStorage();
        edit_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        edit_name.setText(user.getDisplayName());

        edit_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEditImage();
            }
        });

        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {onClickEditName();}
        });
        arrow_left_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {onClickEditName();}
        });

        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {onClickEditEmail();}
        });
        arrow_left_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {onClickEditEmail();}
        });

        edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEditPassword();
            }
        });
        arrow_left_password.setOnClickListener(new View.OnClickListener() {
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

    }

    private void form_signOut() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).form_sign_out();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onClickEditImage(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box_change_avatar);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        change_avatar=dialog.findViewById(R.id.change_avatar);
        change_avatar.setImageDrawable(edit_avatar.getDrawable());
        close_button = dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        change_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });
        Button change_button=dialog.findViewById(R.id.change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=FirebaseAuth.getInstance().getCurrentUser();
                if (mUri == null) {
                    dialog.dismiss();
                    return;
                }
                uploadImageToFirebaseStorage();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(user.getDisplayName())
                        .setPhotoUri(mUri)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user=FirebaseAuth.getInstance().getCurrentUser();
                                    Toast.makeText(getActivity(), R.string.update_image_success, Toast.LENGTH_SHORT).show();
                                    befor_addItemAndReload("Update image .");
                                    Glide.with(getActivity()).load(mUri).error(edit_avatar.getDrawable()).into(edit_avatar);
                                    refresh_activity();
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
    }

    private void onClickEditName() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_form_change_name);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        EditText text_name = dialog.findViewById(R.id.text_name);
        text_name.setText(user.getDisplayName());
        close_button = dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button change_button = dialog.findViewById(R.id.change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (text_name.getText().toString().contains(" ")) {
                    Toast.makeText(getContext(), R.string.name_can_not_contain_spaces, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (text_name.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.name_can_not_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder()
                        .setDisplayName(text_name.getText().toString());

                if (mUri != null) {
                    profileUpdatesBuilder.setPhotoUri(mUri);
                }

                UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user=FirebaseAuth.getInstance().getCurrentUser();
                                    edit_name.setText(text_name.getText());
                                    befor_addItemAndReload("Change name .");
                                    Toast.makeText(getActivity(), R.string.change_name_success, Toast.LENGTH_SHORT).show();
                                    updateData();
                                    refresh_activity();
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
    }

    private void onClickEditEmail(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_form_change_email);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        EditText text_email=dialog.findViewById(R.id.text_email);
        text_email.setText(edit_email.getText());

        close_button = dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button change_button = dialog.findViewById(R.id.change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_email.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(), R.string.email_can_not_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidEmailFormat(text_email.getText().toString())) {
                    Toast.makeText(getContext(), R.string.invalid_email_format, Toast.LENGTH_SHORT).show();
                    return;
                }
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null)
                {
                    return;
                }
                user.updateEmail(text_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user=FirebaseAuth.getInstance().getCurrentUser();
                                    befor_addItemAndReload("Change email .");
                                    refresh_activity();
                                    edit_email.setText(text_email.getText());
                                    Toast.makeText(getActivity(), R.string.change_email_success, Toast.LENGTH_SHORT).show();
                                    updateData();
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
    }

    private void onClickEditPassword() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_form_change_password);
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
                                    Toast.makeText(getActivity(), R.string.password_be_at_least_8_characters, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!password1Str.equals(password2Str)) {
                                    Toast.makeText(getActivity(), R.string.password_not_match, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                user.updatePassword(password1Str)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), R.string.update_password_success, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    refresh_activity();
                                                    befor_addItemAndReload("Change password .");
                                                } else {
                                                    Toast.makeText(getActivity(), R.string.update_password_failed, Toast.LENGTH_SHORT).show();
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
            mUri = data.getData();
            Glide.with(getActivity()).load(mUri).error(edit_avatar.getDrawable()).into(change_avatar);
            Toast.makeText(getActivity(), R.string.image_selected_successfully, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), R.string.no_image_selected, Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImageToFirebaseStorage() {
        StorageReference mountainsRef = storageRef.child(user.getUid()+"/avatar/"+"avatar.jpg");
        UploadTask uploadTask = mountainsRef.putFile(mUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mountainsRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                mUri=task.getResult();
                updateData();
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
        db.addItem(item);
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

    private boolean isValidEmailFormat(String email) {
        // Regular expression to check the email format xx@abc.xyz
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    public void updateData()
    {
        myRef = database.getReference("user_inform");
        String uid = user.getUid();
        String email = user.getEmail();
        String[] parts = email.split("@");
        String name=user.getDisplayName();
        String filename = parts[0] + ".db";
        if(user.getDisplayName()==null)
        {
            name=parts[0];
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("file", filename);
        if(mUri!=null)
        {
            userMap.put("avatar", mUri.toString());
        }
        myRef.child(uid).updateChildren(userMap);
        user= FirebaseAuth.getInstance().getCurrentUser();
    }

    private void getImagefromStorage() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String index = "user_inform/" + uid;
            myRef = database.getReference(index);
            myRef.child("avatar").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                    } else {
                        String URL = task.getResult().getValue(String.class);
                        if (URL != null) {
                            new ImageLoadTask(URL, edit_avatar).execute();
                        } else {
                        }
                    }
                }
            });
        }
    }

}