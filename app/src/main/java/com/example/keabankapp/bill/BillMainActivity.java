package com.example.keabankapp.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BillMainActivity extends AppCompatActivity {
    private static final String TAG = "BillMainActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    //private BillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
        setContentView(R.layout.activity_bill_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(onClickFab);
    }


    /*
        setupFirebaseAuth is to see if there is a user signed in or not.
        if the user is signed in no action is taken.
        If a user is not signed in, a intent in startet to take the user back
        to the login screen.
        The intent has two modifiers. NEW_TASK and CLEAR_TASK.
        NEW_TASK sets the intent as a root in the task manager
        CLEAR_TASK Clears the task log before starting a new task.
        That means that the user cannot go back to the last page
        if this was triggered
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Intent intent = new Intent(BillMainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            }
        };
    }
    //Sets a listener on floating Action Button/fab and displays a snackbar
    private View.OnClickListener onClickFab = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BillMainActivity.this, BillPaymentActivity.class);
            startActivity(intent);
        }
    };
    //Runs when activity loads, and actions happens
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called ");
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    //runs when activity stops.
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called");
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }

    }
}
