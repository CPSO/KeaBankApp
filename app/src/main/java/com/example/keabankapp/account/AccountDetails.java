package com.example.keabankapp.account;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.MainActivity;
import com.example.keabankapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountDetails extends AppCompatActivity {
    private static final String TAG = "AccountDetails";
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        Log.d(TAG, "onCreate: called");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getIncomingIntent();
    }

    //Graps the intent send from the MainActivity and binds the document collections
    private void getIncomingIntent(){

        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");
        String accountID;
        String accountPath;

        if(getIntent().hasExtra("accountID") && getIntent().hasExtra("accountPath")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String idForAccount = getIntent().getStringExtra("accountID");
            String pathForAccount = getIntent().getStringExtra("accountPath");
            String nameForAccount = getIntent().getStringExtra("accountName");

            Log.d(TAG, "getIncomingIntent: accountID: " + idForAccount);
            Log.d(TAG, "getIncomingIntent: accountPath: " + pathForAccount);
            Log.d(TAG, "getIncomingIntent: accountName: " + nameForAccount);
        } else {
            Log.d(TAG, "getIncomingIntent: No intent was had");
        }
    }

    //Checks the state of the user that is sign in.
    //ether the user is active or is signing out
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(AccountDetails.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    //Runs when activity loads, and actions happens
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called ");
        //adapter.startListening();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    //runs when activity stops.
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called");
        //adapter.stopListening();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
