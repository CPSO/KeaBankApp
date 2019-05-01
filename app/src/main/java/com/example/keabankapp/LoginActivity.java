package com.example.keabankapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText uMail, uPassword;
    private Button bSignIn, bCreateUser;
    private static final String TAG ="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bSignIn = findViewById(R.id.btnSignIn);
        bCreateUser = findViewById(R.id.btnCreateAccount);
        uMail = findViewById(R.id.etMail);
        uMail = findViewById(R.id.etPassword);
        bSignIn.setOnClickListener(onClickSignIn);
        bCreateUser.setOnClickListener(onClickCreateUSer);
    }

    private View.OnClickListener onClickSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickSignIn: Called ");

        }
    };

    private View.OnClickListener onClickCreateUSer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickCreateUser: Called ");


        }
    };


}
