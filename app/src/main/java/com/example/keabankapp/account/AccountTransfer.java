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
import android.widget.Toast;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.MainActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.adapter.AccountAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    private String accountToID;
    private double accountToBalance;
    private double accountFromBalance;
    private int check1,check2;
    






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_transfer);
        Log.d(TAG, "onCreateAccountTransfer: Called");
        etAmount = findViewById(R.id.etTransfAmount);
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
        final List<Double> accountsBalance = new ArrayList<>();
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
                        double accountBalance = document.getDouble("aAmount");
                        Log.d(TAG, "onComplete: " + accountBalance);

                        accounts.add(accountName);
                        accountsID.add(accountID);
                        accountsBalance.add(accountBalance);
                        accountToID = accountID;
                        accountToBalance = accountBalance;
                        Log.d(TAG, "onComplete: "+ accountToBalance);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        spinnerToAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: spinner has chosen: " + parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: spinner has picked id: " + accountsID.get(position));
                Log.d(TAG, "onItemSelected: spinner has picked account with balance:  " + accountsBalance.get(position));
                accountToID = accountsID.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getTransferMoney(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference accountFromRef = db.collection(userId).document("accounts").collection("accounts").document(accountID);
        DocumentReference accountToRef = db.collection(userId).document("accounts").collection("accounts").document(accountToID);



        final Task<DocumentSnapshot> getAccountFrom = accountFromRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: ");
            }
        });
        final Task<DocumentSnapshot> getAccountTo = accountToRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
            }
        });


        Tasks.whenAllComplete(getAccountFrom,getAccountTo).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
               if (getAccountFrom.getResult().exists() && getAccountTo.getResult().exists()){
                   Log.d(TAG, "Task onComplete: Found accounts for transactions ");
                   accountFromBalance = getAccountFrom.getResult().getDouble("aAmount");
                   accountToBalance = getAccountTo.getResult().getDouble("aAmount");
                   Log.d(TAG, "Task onComplete: Printing balance of accounts: ");
                   Log.d(TAG, "onComplete: accountToBalance: " + accountToBalance);
                   Log.d(TAG, "onComplete: accountFromBalance: " + accountFromBalance);
                   transferMoney();

               } else {
                   Log.d(TAG, "Task onComplete: Failed to find accounts ");
                   Toast.makeText(AccountTransfer.this,"No accounts found",Toast.LENGTH_LONG).show();
               }

            }
        });
    }

    private void transferMoney(){
        //creates a referance to the collection in Firestore

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference accountFromRef = db.collection(userId).document("accounts").collection("accounts").document(accountID);
        Log.d(TAG, "transferMoney: from account with id: " + accountID);
        DocumentReference accountToRef = db.collection(userId).document("accounts").collection("accounts").document(accountToID);
        Log.d(TAG, "transferMoney: to account with id " + accountToID);

        double valueFromET;
        String text = etAmount.getText().toString();
        valueFromET = Double.parseDouble(text);
        final double newBalance = (accountFromBalance - valueFromET);
        final double toAccountBalance = (accountToBalance + valueFromET);

        //final Task<DocumentSnapshot> updateAccountFrom = accountFromRef.get();
        //final Task<DocumentSnapshot> updateAccountTo = accountToRef.update("aAmount",toAccountBalance);

        //Tasks.whenAllComplete()

        /*
        try {
            Log.d(TAG, "onClickSubmit: trying parse double " + valueFromET);

            accountToRef.update(
                    "aAmount",toAccountBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AccountTransfer.this, "Balance Added",
                                Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(AccountTransfer.this, "Failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e);
                }
            });

            accountFromRef.update("aAmount",newBalance).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Account from balance" + newBalance);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } catch (Exception e1){
            e1.printStackTrace();
            Log.d(TAG, "onClickSubmit: parsing failed ");
        }
        */

    }

    private View.OnClickListener onClickSubmitTransf = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickTransf: called");
            getTransferMoney();
        }
    };






    private void init(){

        btnSubmit = findViewById(R.id.btnTransfSubmit);
        spinnerFromAccount = findViewById(R.id.spinnerFromAccount);
        spinnerToAccount = findViewById(R.id.spinnerToAccount);
        btnSubmit.setOnClickListener(onClickSubmitTransf);


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
