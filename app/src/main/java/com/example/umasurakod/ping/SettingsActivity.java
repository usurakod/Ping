package com.example.umasurakod.ping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private StorageReference UserProfileImageRef;
    private ProgressDialog lodingBar;
    private Toolbar settingsToolBar;

    private static int galleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeFields();

        userName.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        settingsToolBar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retriveInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPick);
            }
        });
    }

    private void initializeFields() {
        updateAccountSettings = (Button)findViewById(R.id.update_Settings_Button);
        userName = (EditText)findViewById(R.id.user_name);
        userStatus = (EditText)findViewById(R.id.profile_status);
        userProfileImage = (CircleImageView)findViewById(R.id.profile_image);
        lodingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            Uri imageUri= data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                lodingBar.setTitle("Set Profile Image");
                lodingBar.setMessage("Please wait, your profile image is updating ...");
                lodingBar.setCanceledOnTouchOutside(false);
                lodingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,"Profile Image Uploaded Successfully!",Toast.LENGTH_SHORT).show();

                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                        rootRef.child("User").child(currentUserId).child("image")
                                .setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this,"Profile Image Uploaded Successfully!",Toast.LENGTH_SHORT).show();
                                            lodingBar.dismiss();
                                        }
                                        else{
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                                            lodingBar.dismiss();
                                        }
                                    }
                                });
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                            lodingBar.dismiss();
                        }
                    }
                });

            }
        }
    }

    private void updateSettings() {
         String setUserName = userName.getText().toString();
         String setUserStatus = userStatus.getText().toString();

         if(TextUtils.isEmpty(setUserName)){
             Toast.makeText(SettingsActivity.this, "Please Enter User Name...", Toast.LENGTH_SHORT).show();
         }
         if(TextUtils.isEmpty(setUserStatus)){
             Toast.makeText(SettingsActivity.this, "Please write your status...", Toast.LENGTH_SHORT).show();
         }
         else{
             HashMap<String,Object> profileInfoMap = new HashMap<>();
             profileInfoMap.put("uid",currentUserId);
             profileInfoMap.put("name", setUserName);
             profileInfoMap.put("status", setUserStatus);
             rootRef.child("User").child(currentUserId).updateChildren(profileInfoMap)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 sendUserToMain();
                                 Toast.makeText(SettingsActivity.this, "Your profile updated successfully!", Toast.LENGTH_SHORT).show();
                             }
                             else {
                                 String message = task.getException().toString();
                                 Toast.makeText(SettingsActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();

                             }
                         }
                     });
         }

    }
    private void sendUserToMain() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }

    private void retriveInfo() {
        String currentUserIDA = mAuth.getCurrentUser().getUid();
        rootRef.child("User").child(currentUserIDA).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                            String retriveUserName = (String) dataSnapshot.child("user").getValue();
                            String retriveUserStatus = (String) dataSnapshot.child("status").getValue();
                            String retriveUserImage = (String) dataSnapshot.child("image").getValue();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveUserStatus);

                            Picasso.get().load(retriveUserImage).into(userProfileImage);

                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retriveUserName = (String) dataSnapshot.child("name").getValue();
                            String retriveUserStatus = (String) dataSnapshot.child("status").getValue();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveUserStatus);
                        }
                        else{
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set and update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
