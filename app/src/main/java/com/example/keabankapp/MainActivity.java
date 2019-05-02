package com.example.keabankapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.account_page:
                Log.d(TAG, "onOptionsItemSelected: account_page");
                return true;
            case R.id.card_page:
                Log.d(TAG, "onOptionsItemSelected: card_page");
                return true;
            case R.id.contact_page:
                Log.d(TAG, "onOptionsItemSelected: contact_page");
                return true;
            case R.id.user_page:
                Log.d(TAG, "onOptionsItemSelected: user_page");
                return true;
            case R.id.sign_out_page:
                Log.d(TAG, "onOptionsItemSelected: sign_out_page");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
