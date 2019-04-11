package com.example.umasurakod.ping;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeFields();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retriveInfo();
    }

    private void initializeFields() {
        updateAccountSettings = (Button)findViewById(R.id.update_Settings_Button);
        userName = (EditText)findViewById(R.id.user_name);
        userStatus = (EditText)findViewById(R.id.profile_status);
        userProfileImage = (CircleImageView)findViewById(R.id.profile_image);
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
             HashMap<String,String> profileInfoMap = new HashMap<>();
             profileInfoMap.put("uid",currentUserId);
             profileInfoMap.put("name", setUserName);
             profileInfoMap.put("status", setUserStatus);
             rootRef.child("User").child(currentUserId).setValue(profileInfoMap)
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
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retriveUserName = (String) dataSnapshot.child("name").getValue();
                            String retriveUserStatus = (String) dataSnapshot.child("status").getValue();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveUserStatus);
                        }
                        else{
                            Toast.makeText(SettingsActivity.this, "Please set and update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
