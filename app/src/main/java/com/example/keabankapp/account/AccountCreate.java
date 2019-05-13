package com.example.keabankapp.account;

import android.content.Intent;
import android.net.IpSecManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.keabankapp.CreateUserActivity;
import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.MainActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AccountCreate extends AppCompatActivity {
    private static final String TAG = "AccountCreate";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference accountsListRef = db.collection(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).document("accounts").collection("accounts");
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    String accountType[] = {"savings","budget","pension", "default", "business"};
    EditText accountName;
    Spinner spinner;
    Button btnCreateNewAccount;
    private String selectedAccountType;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);
        spinner = findViewById(R.id.spinnerAccType);
        btnCreateNewAccount = findViewById(R.id.btnSaveNewAccount);
        btnCreateNewAccount.setOnClickListener(onClickCreateAccount);
        accountName = findViewById(R.id.etAccName);
        setupFirebaseAuth();
        spinnerAdapter();
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
                    Intent intent = new Intent(AccountCreate.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void spinnerAdapter(){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, accountType);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: get selected item " + parent.getSelectedItem());
                Log.d(TAG, "onItemSelected: get slecet item at pos " + parent.getItemAtPosition(position));
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.d(TAG, "onItemSelected: just show the user id " + userId);

                selectedAccountType = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: account " + selectedAccountType);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });

    }

    private View.OnClickListener onClickCreateAccount = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "onClick: called");

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String aName = accountName.getText().toString();
                double aAmount = 0.00;
                String aType = selectedAccountType;

                DocumentReference accountRef = FirebaseFirestore.getInstance()
                        .collection(userId).document("accounts").collection("accounts").document();
                accountRef.set(new AccountModel(aName,aAmount,aType));

            Toast.makeText(AccountCreate.this, "Created a new account",
                    Toast.LENGTH_SHORT).show();

                finish();


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
