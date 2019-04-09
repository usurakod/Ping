package com.example.umasurakod.ping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentuser;
    private Button loginButton, phoneLoginButton;
    private EditText userEmail,userPwd;
    private TextView forgotPwd, loginNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();

        loginNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegister();
            }
        });
    }

    private void InitializeFields() {
        loginButton = (Button)findViewById(R.id.login_button);
        phoneLoginButton = (Button)findViewById(R.id.phone_login_button);
        userEmail = (EditText)findViewById(R.id.login_email);
        userPwd = (EditText)findViewById(R.id.login_pwd);
        forgotPwd = (TextView)findViewById(R.id.forgot_pwd_link);
        loginNew = (TextView)findViewById(R.id.need_newAcc_link);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentuser != null){
            sendUserToMain();
        }
    }

    private void sendUserToMain() {
        Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(loginIntent);
    }
    private void sendUserToRegister() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}
