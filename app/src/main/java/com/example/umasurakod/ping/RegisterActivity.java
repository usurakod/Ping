package com.example.umasurakod.ping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {


    private Button registerButton;
    private EditText userEmail,userPwd;
    private TextView forgotPwd, alreadyHaveAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeFields();

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLogin();
            }
        });
    }

    private void InitializeFields() {
        registerButton = (Button)findViewById(R.id.Register_button);
        userEmail = (EditText)findViewById(R.id.register_email);
        userPwd = (EditText)findViewById(R.id.register_pwd);
        alreadyHaveAcc = (TextView)findViewById(R.id.alterady_haveAcc_link);

    }

    private void sendUserToLogin() {
        Intent registerIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(registerIntent);
    }

}
