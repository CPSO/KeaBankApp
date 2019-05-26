package com.example.keabankapp.bill;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BillPaymentActivity extends AppCompatActivity {
    //TAG
    private static final String TAG = "BillPaymentActivity";
    //Widgets
    private EditText datePicker,paymentAmount,paymentName,accountReciver;
    private Spinner spinnerAccount;
    private TextView accountAmountTV;
    private Button buttonSubmit;
    private CheckBox autoPayment;
    //Firebase/Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Variables
    private String userID;
    private String selectedAccountID;
    private Double selectedAccountBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
        setContentView(R.layout.activity_bill_payment);
        init();
        //setupSpinner();
    }
    private void setupSpinner(){
        CollectionReference accountRef = db.collection("users").document(userID).collection("accounts");
        final List<String> accountsName = new ArrayList<>();
        final List<String> accountsID = new ArrayList<>();
        final List<Double> accountsBalance = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, accountsName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(adapter);
        accountRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String accountName = document.getString("aName");
                        String accountID = document.getId();
                        double accountBalance = document.getDouble("aAmount");
                        Log.d(TAG, "onComplete: setupSpinner Completet getting data");

                        accountsName.add(accountName);
                        accountsID.add(accountID);
                        accountsBalance.add(accountBalance);

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAccountID = accountsID.get(position);
                Log.d(TAG, "onItemSelected: getting account id for position: " + position + " " + accountsID.get(position));
                selectedAccountBalance = accountsBalance.get(position);
                accountAmountTV.setText(Double.toString(selectedAccountBalance));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAccountID = accountsID.get(0);
                Log.d(TAG, "onNothingSelected: setting selected ID to index 0");
                selectedAccountBalance = accountsBalance.get(0);
                accountAmountTV.setText(Double.toString(selectedAccountBalance));

            }
        });
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
        Log.d(TAG, "setupFirebaseAuth: display userId" + userID);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                    userID = user.getUid();
                    setupSpinner();
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Intent intent = new Intent(BillPaymentActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
    private void init(){
        datePicker = findViewById(R.id.etDatePicker);
        paymentAmount = findViewById(R.id.etPaymentAmount);
        paymentName = findViewById(R.id.etNameForPayment);
        accountReciver = findViewById(R.id.etAccountToInfo);
        spinnerAccount = findViewById(R.id.spinnerPaymentAccount);
        accountAmountTV = findViewById(R.id.tvSelectedAccountBalance);
        buttonSubmit = findViewById(R.id.btnSubmitPayment);
        autoPayment = findViewById(R.id.cbAutoPay);
    }
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
