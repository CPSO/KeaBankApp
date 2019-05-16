package com.example.keabankapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText uMail, uPassword;
    private Button bSignIn, bCreateUser, bResetPw;
    private static final String TAG ="LoginActivity";
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Log TAG
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();                 //assign mAuth to the FirebaseAuth instance
        bSignIn = findViewById(R.id.btnSignIn);
        bCreateUser = findViewById(R.id.btnCreateAccount);
        uMail = findViewById(R.id.etMail);
        uPassword = findViewById(R.id.etPassword);
        bSignIn.setOnClickListener(onClickSignIn);
        //bSignIn.setOnClickListener(this);
        bCreateUser.setOnClickListener(onClickCreateUSer);
        bResetPw = findViewById(R.id.btnResetPw);
        bResetPw.setOnClickListener(OnClickResetPw);
        setupFirebaseAuth();                                //Runs the setupFirebaseAuth method

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSignIn){
            Log.d(TAG, "onClick: login_pressed");
            //check if the fields are filled out
            if(!isEmpty(uMail.getText().toString())
                    && !isEmpty(uPassword.getText().toString())){
                Log.d(TAG, "onClick: attempting to authenticate.");

                FirebaseAuth.getInstance().signInWithEmailAndPassword(uMail.getText().toString(),
                        uPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ERROR on login");
                        //Snackbar.make(getCurrentFocus().getRootView(), R.string.signInAuthError, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }else{
                // Snackbar.make(getCurrentFocus().getRootView(), R.string.signInError, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private View.OnClickListener onClickSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickSignIn: Called ");

            if (!isEmpty(uMail.getText().toString())&& !isEmpty(uPassword.getText().toString())){
                Log.d(TAG, "onClick: not empty called");

                FirebaseAuth.getInstance().signInWithEmailAndPassword(uMail.getText().toString(),uPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,getString(R.string.toastLoginError), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            /*
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
             */

        }
    };





    private View.OnClickListener onClickCreateUSer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickCreateUser: Called ");
            Intent intent = new Intent(LoginActivity.this, CreateUserActivity.class);
            startActivity(intent);

        }
    };

    private View.OnClickListener OnClickResetPw = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickResetPw: Called");
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
            Log.d(TAG, "onClickResetPw: intent called ");
        }
    };

    private boolean isEmpty(String string){
        return string.equals("");
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }

            }
        };
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    /*
     * onStop method is called when the page stops to load
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

}
