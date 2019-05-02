package com.example.keabankapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateUserActivity extends AppCompatActivity {
    EditText uFName, uLName, uMail, uPhone;
    EditText uAdress, uZipCode;
    TextView bankLocCPH, bankLocOds;
    Button btnCreateUser;
    private static final String TAG = "CreateUserActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Log.d(TAG, "onCreate: Called");
        init();
        btnCreateUser.setOnClickListener(onClickCreateUser);


    }

    private View.OnClickListener onClickCreateUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickCreateUser: Called ");
            Intent intent = new Intent(CreateUserActivity.this, LoginActivity.class);
            startActivity(intent);
            
        }
    };


    private void init(){
        uFName = findViewById(R.id.etUserFName);
        uLName = findViewById(R.id.etUserLName);
        uMail = findViewById(R.id.etUserEmail);
        uPhone = findViewById(R.id.etUserPhone);
        uAdress = findViewById(R.id.etAdressName);
        uZipCode = findViewById(R.id.etZipCode);
        bankLocCPH = findViewById(R.id.tvCPH);
        bankLocOds = findViewById(R.id.tvOdense);
        btnCreateUser = findViewById(R.id.btnSignUp);

    }
}
