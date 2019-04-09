package com.example.umasurakod.ping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {


    private Button creeateAccountButton;
    private EditText userEmail,userPwd;
    private TextView forgotPwd, alreadyHaveAcc;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeFields();
        mAuth = FirebaseAuth.getInstance();

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLogin();
            }
        });
        creeateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String email = userEmail.getText().toString();
        String password = userPwd.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter the email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter the password...",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Creating New Account...");
            loadingBar.setMessage("Please wait while creting account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Account created successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error is "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });

            }

        }

    private void InitializeFields() {
        creeateAccountButton = (Button)findViewById(R.id.Register_button);
        userEmail = (EditText)findViewById(R.id.register_email);
        userPwd = (EditText)findViewById(R.id.register_pwd);
        alreadyHaveAcc = (TextView)findViewById(R.id.alterady_haveAcc_link);
        loadingBar = new ProgressDialog(this);

    }

    private void sendUserToLogin() {
        Intent registerIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(registerIntent);
    }

}
