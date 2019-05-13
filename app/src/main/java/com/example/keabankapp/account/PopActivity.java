package com.example.keabankapp.account;

import android.app.Activity;
import android.icu.util.ValueIterator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.keabankapp.R;



public class PopActivity extends Activity {
    Button btnSubmit;
    EditText etAmount;

    private static final String TAG = "PopActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        btnSubmit = findViewById(R.id.btnDepositConfirm);
        etAmount = findViewById(R.id.etDepositAmount);
        btnSubmit.setOnClickListener(OnClickListenerSubmit);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
    private View.OnClickListener OnClickListenerSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            double amount = Double.parseDouble(String.valueOf(etAmount.getText()));

            Log.d(TAG, "onClick: ");
            Log.d(TAG, "onClick: " + amount);


        }
    };
}
