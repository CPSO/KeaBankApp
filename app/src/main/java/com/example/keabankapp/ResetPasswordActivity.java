package com.example.keabankapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText etUserEmail;
    Button btnSubmitReset;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
        setTitle();
    }

    private void init(){
        etUserEmail = findViewById(R.id.etResetPwEmail);
        btnSubmitReset = findViewById(R.id.btnResetPassword);

    }
    private void setTitle(){
        this.setTitle("Reset Password");

    }
}
