package com.example.keabankapp.account;

import android.content.Intent;
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

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.MainActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.adapter.AccountAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountTransfer extends AppCompatActivity {
    private static final String TAG = "AccountTransfer";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    Spinner spinnerToAccount;
    Button btnSubmit;
    EditText etAmount;
    Spinner spinnerFromAccount;
    private String accountID;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_transfer);
        Log.d(TAG, "onCreateAccountTransfer: Called");
        setupFirebaseAuth();
        init();
        setupSpinner();
        getIncomingIntent();
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
                    Intent intent = new Intent(AccountTransfer.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    //Graps the intent send from the MainActivity and binds the document collections
    private void getIncomingIntent(){

        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("accountID")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String idForAccount = getIntent().getStringExtra("accountID");
            accountID = idForAccount;
            Log.d(TAG, "getIncomingIntent: accountID: " + idForAccount);
        } else {
            Log.d(TAG, "getIncomingIntent: No intent was had");
        }
    }


    private void setupSpinner(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference accountRef = db.collection(userId).document("accounts").collection("accounts");
        final List<String> accounts = new ArrayList<>();
        final List<String> accountsID = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, accounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToAccount.setAdapter(adapter);
        accountRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String accountName = document.getString("aName");
                        String accountID = document.getId();
                        accounts.add(accountName);
                        accountsID.add(accountID);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        spinnerToAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: spinner has chosen" + parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: spinner has picked id: " + accountsID.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void transferMoney(){
        //creates a referance to the collection in Firestore
        DocumentReference accountRef = db.collection(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).document("accounts").collection("accounts").document(accountID);

    }






    private void init(){

        etAmount = findViewById(R.id.etTransfAmount);
        btnSubmit = findViewById(R.id.btnTransfSubmit);
        spinnerFromAccount = findViewById(R.id.spinnerFromAccount);
        spinnerToAccount = findViewById(R.id.spinnerToAccount);


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
