package com.example.keabankapp.account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.keabankapp.R;

public class AccountDetails extends AppCompatActivity {
    private static final String TAG = "AccountDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        getIncomingIntent();
    }

    //Graps the intent send from the MainActivity and binds the document collections
    private void getIncomingIntent(){

        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");
        String courseID;
        String coursePath;

        if(getIntent().hasExtra("courseID") && getIntent().hasExtra("coursePath")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String idForCourse = getIntent().getStringExtra("courseID");
            String pathForCourse = getIntent().getStringExtra("coursePath");
            String nameForCourse = getIntent().getStringExtra("courseName");

            Log.d(TAG, "getIncomingIntent: courseID: " + idForCourse);
            Log.d(TAG, "getIncomingIntent: coursePath: " + pathForCourse);
            Log.d(TAG, "getIncomingIntent: courseName: " + nameForCourse);
        } else {
            Log.d(TAG, "getIncomingIntent: No intent was had");
        }
    }
}
