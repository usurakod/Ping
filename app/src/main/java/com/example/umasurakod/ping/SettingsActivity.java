package com.example.umasurakod.ping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, status;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeFields();
    }

    private void initializeFields() {
        updateAccountSettings = (Button)findViewById(R.id.update_Settings_Button);
        userName = (EditText)findViewById(R.id.user_name);
        status = (EditText)findViewById(R.id.profile_status);
        userProfileImage = (CircleImageView)findViewById(R.id.profile_image);
    }
}
