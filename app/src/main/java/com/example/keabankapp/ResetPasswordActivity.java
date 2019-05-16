package com.example.keabankapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";
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
        mAuth = FirebaseAuth.getInstance();
    }

    private void init(){
        etUserEmail = findViewById(R.id.etResetPwEmail);
        btnSubmitReset = findViewById(R.id.btnResetPassword);
        btnSubmitReset.setOnClickListener(onClickResetSubmit);

    }
    private void setTitle(){
        this.setTitle("Reset Password");
    }

    private View.OnClickListener onClickResetSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickResetSubmit: Clicked ");
            mAuth.sendPasswordResetEmail(etUserEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: Email sent");
                        Toast.makeText(ResetPasswordActivity.this,"Email with reset sent.", Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        Log.d(TAG, "onComplete: Email Failed");
                        Toast.makeText(ResetPasswordActivity.this,task.getException().getMessage() ,Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    };
}
