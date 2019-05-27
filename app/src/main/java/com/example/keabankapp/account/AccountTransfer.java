package com.example.keabankapp.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountTransactionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountTransfer extends AppCompatActivity {
    private static final String TAG = "AccountTransfer";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    Spinner spinnerToAccount,spinnerGlobal;
    Button btnSubmit,btnGlobalSubmit,btnGetData;
    EditText etAmount,etAccountToEmail;
    TextView tvAccountName;
    Switch aSwitch;
    TextView tvToTitle,tvSelectAccount,tvSelectEmail;
    private String accountID,accountToID;
    private double accountToBalance,accountFromBalance;
    private SparseIntArray nemCode = new SparseIntArray();
    private double valueFromET;
    private String globalUserId;
    private boolean isChecked = false;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_transfer);
        Log.d(TAG, "onCreateAccountTransfer: Called");
        etAmount = findViewById(R.id.etTransfAmount);
        setupFirebaseAuth();
        init();
        getIncomingIntent();
        setupSpinner();
        setupNemCode();
        setElements();
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
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference accountSelectedName = db.collection("users").document(userId).collection("accounts").document(accountID);
        final CollectionReference accountRef = db.collection("users").document(userId).collection("accounts");
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
                globalUserId = userId;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        accountSelectedName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String accountName = documentSnapshot.getString("aName");
                tvAccountName.setText(accountName);
                Log.d(TAG, "onSuccess: ACCOUNTSELECTNAME !!!!");
            }
        });
    }


    private void searchForUser(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = etAccountToEmail.getText().toString();
        final Query collectionReference = db.collection("users").whereEqualTo("uEmail", userEmail);

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Log.d(TAG, "onSuccess: " + document.getId() + " - " + document.getData());
                        globalUserId = document.getId();
                        setupGlobalSpinner();


                    }
                } else {
                    Log.d(TAG, "No User found: ");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed Query to Firestore" + e.getMessage());
            }
        });


        db.collection("users")
                .whereEqualTo("uEmail", userEmail) // <-- This line
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, "onComplete:");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void setupGlobalSpinner(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference accountRef = db.collection("users").document(globalUserId).collection("accounts");
        final List<String> accounts = new ArrayList<>();
        final List<String> accountsID = new ArrayList<>();
        final List<Double> accountsBalance = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, accounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGlobal.setAdapter(adapter);
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
        spinnerGlobal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: spinner has chosen: " + parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: spinner has picked id: " + accountsID.get(position));
                Log.d(TAG, "onItemSelected: spinner has picked account with balance:  " + accountsBalance.get(position));
                accountToID = accountsID.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String aString = parent.getItemAtPosition(0).toString();
                accountToID = aString;
            }
        });
    }


    private void getTransferMoney(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        if (globalUserId.isEmpty()){
            Log.d(TAG, "getTransferMoney: Needs data for sender");
        }else {
        DocumentReference accountFromRef = db.collection("users").document(userId).collection("accounts").document(accountID);
        DocumentReference accountToRef = db.collection("users").document(globalUserId).collection("accounts").document(accountToID);



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
                double valueFromET;
                String text = etAmount.getText().toString();
                if (text.isEmpty()) {
                    Log.d(TAG, "onComplete: INDPUT IS EMPTY");
                } else {
                    valueFromET = Double.parseDouble(text);
                    if (getAccountFrom.getResult().exists() && getAccountTo.getResult().exists()) {
                        Log.d(TAG, "Task onComplete: Found accounts for transactions ");
                        accountFromBalance = getAccountFrom.getResult().getDouble("aAmount");
                        accountToBalance = getAccountTo.getResult().getDouble("aAmount");
                        Log.d(TAG, "Task onComplete: Printing balance of accounts: ");
                        Log.d(TAG, "onComplete: accountToBalance: " + accountToBalance);
                        Log.d(TAG, "onComplete: accountFromBalance: " + accountFromBalance);
                        if (accountFromBalance < valueFromET) {
                            Log.d(TAG, "onComplete: Amount to hight");
                            Toast.makeText(AccountTransfer.this, "Transfer amount to hight", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "onComplete: calling transfer ");
                            nemID();
                        }

                    } else {
                        Log.d(TAG, "Task onComplete: Failed to find accounts ");
                        Toast.makeText(AccountTransfer.this, "No accounts found", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }
    }

    private void nemID(){
        int size = nemCode.size();
        Random r = new Random();
        int randomNumber = r.nextInt(size);
        int selectedKey = nemCode.keyAt(randomNumber);
        final int selectedValueInt = nemCode.valueAt(randomNumber);
        final String selectedValueString = Integer.toString(selectedValueInt);
        Log.d(TAG, "nemID: Another randommer values: " + randomNumber);
        Log.d(TAG, "nemID: Selecting key: " + selectedKey);
        Log.d(TAG, "nemID: Selecting key: " + selectedValueInt);


        final EditText nemidInputText = new EditText(this);
        nemidInputText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("NEM ID Code")
                .setMessage("Enter code for key: " + selectedKey)
                .setView(nemidInputText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nemIDValue = String.valueOf(nemidInputText.getText());
                        if (nemIDValue.equals(selectedValueString)){
                            Log.d(TAG, "onClick: " + nemIDValue + " = " + selectedValueString);
                            Toast.makeText(AccountTransfer.this,"Sending Money", Toast.LENGTH_LONG).show();
                            transferMoney();

                        } else {
                            Log.d(TAG, "onClick: " + nemIDValue + " != " + selectedValueString);
                            Toast.makeText(AccountTransfer.this,"Wrong Value", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

    private void transferMoney(){
        //creates a referance to the collection in Firestore

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference accountFromRef = db.collection("users").document(userId).collection("accounts").document(accountID);
        Log.d(TAG, "transferMoney: from account with id: " + accountID);
        DocumentReference accountToRef = db.collection("users").document(globalUserId).collection("accounts").document(accountToID);
        Log.d(TAG, "transferMoney: to account with id " + accountToID);


        String text = etAmount.getText().toString();
        valueFromET = Double.parseDouble(text);
        final double newBalance = (accountFromBalance - valueFromET);
        final double toAccountBalance = (accountToBalance + valueFromET);


        WriteBatch batch = db.batch();
        batch.update(accountFromRef,"aAmount",newBalance);
        batch.update(accountToRef,"aAmount",toAccountBalance);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Finish money transfer");
                    makeTransactionHistory();
                    finish();
                } else {
                    Log.d(TAG, "onComplete: Something went wrong" + task.getException());
                }
            }
        });
        finish();
    }
    private void makeTransactionHistory(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference accountTransactionFrom = db.collection("users").document(userId).collection("accounts").document(accountID)
                .collection("transactions");
        final CollectionReference accountTransactionTo = db.collection("users").document(globalUserId).collection("accounts").document(accountToID)
                .collection("transactions");

        final String tType = "transfer";
        final Timestamp tTimestamp = Timestamp.now();
        final double tAmount = valueFromET;
        final String tDocumentId = accountID;
        final String tAccountToId = accountToID;

        final Task<DocumentReference> addAccountTransfer = accountTransactionFrom.add(new AccountTransactionModel(tType,tAccountToId,tDocumentId,tTimestamp,tAmount)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: addAccountTransfer");
            }
        });
        final Task<DocumentReference> addAccountTransferTo = accountTransactionTo.add(new AccountTransactionModel(tType,tAccountToId,tDocumentId,tTimestamp,tAmount)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: addAccountTransferTo");
            }
        });

        Tasks.whenAllComplete(addAccountTransfer,addAccountTransferTo).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                Log.d(TAG, "onComplete: All Transfer Notes added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: some Transfer notes failed");
            }
        });



    }

    private View.OnClickListener onClickSubmitTransf = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickTransf: called");
            getTransferMoney();
        }
    };
    
    private View.OnClickListener onClickGetData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: Get Data pressed");
            if (!validateForm()) {
                return;
            }
            searchForUser();
        }
    };

    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                btnGetData.setEnabled(true);
                etAccountToEmail.setEnabled(true);
                spinnerGlobal.setEnabled(true);
                btnGetData.setVisibility(View.VISIBLE);
                spinnerGlobal.setVisibility(View.VISIBLE);
                etAccountToEmail.setVisibility(View.VISIBLE);
                tvSelectEmail.setVisibility(View.VISIBLE);
                tvSelectAccount.setVisibility(View.VISIBLE);
                globalUserId = "";
                spinnerToAccount.setEnabled(false);
                spinnerToAccount.setVisibility(View.GONE);
                tvToTitle.setVisibility(View.GONE);

            } else {
                btnGetData.setEnabled(false);
                etAccountToEmail.setEnabled(false);
                spinnerGlobal.setEnabled(false);
                etAccountToEmail.setVisibility(View.GONE);
                spinnerGlobal.setVisibility(View.GONE);
                btnGetData.setVisibility(View.GONE);
                tvSelectEmail.setVisibility(View.GONE);
                tvSelectAccount.setVisibility(View.GONE);
                spinnerToAccount.setEnabled(true);
                spinnerToAccount.setVisibility(View.VISIBLE);
                tvToTitle.setVisibility(View.VISIBLE);
                globalUserId = "";

            }
        }
    };

    private void setElements(){
        btnGetData.setEnabled(false);
        etAccountToEmail.setEnabled(false);
        spinnerGlobal.setEnabled(false);
        etAccountToEmail.setVisibility(View.GONE);
        spinnerGlobal.setVisibility(View.GONE);
        btnGetData.setVisibility(View.GONE);
        tvSelectEmail.setVisibility(View.GONE);
        tvSelectAccount.setVisibility(View.GONE);

    }



    private boolean validateForm() {
        boolean valid = true;

        String acEmail = etAccountToEmail.getText().toString();
        if (TextUtils.isEmpty(acEmail)) {
            etAccountToEmail.setError("Required.");
            valid = false;
        } else {
            etAccountToEmail.setError(null);
        }
        return valid;
    }



    private void setupNemCode(){
        //#Key - Value
        nemCode.put(1278,3298);
        nemCode.put(4565,9137);
        nemCode.put(8264,7304);
        nemCode.put(7615,6931);
    }


    private void init(){

        btnSubmit = findViewById(R.id.btnTransfSubmit);
        //spinnerFromAccount = findViewById(R.id.accountFromName);
        spinnerToAccount = findViewById(R.id.spinnerToAccount);
        spinnerGlobal = findViewById(R.id.spinnerGlobal);
        btnSubmit.setOnClickListener(onClickSubmitTransf);
        tvAccountName = findViewById(R.id.tvAccountName);
        etAccountToEmail = findViewById(R.id.etAccountEmail);
        //btnGlobalSubmit.setOnClickListener(onClickGlobalSubmit);
        btnGetData = findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(onClickGetData);
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(changeListener);
        tvToTitle = findViewById(R.id.textView11);
        tvSelectAccount = findViewById(R.id.tvPickAccountTitle);
        tvSelectEmail = findViewById(R.id.tvEnterEmailTitle);




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
