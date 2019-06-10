package com.example.keabankapp.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.adapter.AccountTransferAdapter;
import com.example.keabankapp.models.AccountTransactionModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Objects;


public class AccountDetails extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AccountDetails";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String accName;
    private String accountID;
    private String pathForAccount;
    private String accountType;
    private TextView tvAccountName, tvAccountType, tvAccountBalance,tvTransactions;
    private String DocumentID;
    private AccountTransferAdapter adapter;
    private RecyclerView recyclerView;
    private double balance;
    Button btnDepositMoney, btnTransferMoney,btnWithdrawMoney;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        Log.d(TAG, "onCreate: called");
        getIncomingIntent();
        setupFirebaseAuth();
        setTitle();
        loadDataFromFirestore();
        setUpRecyclerView();
        tvAccountName = findViewById(R.id.tvADName);
        tvAccountType = findViewById(R.id.tvADType);
        tvAccountBalance = findViewById(R.id.tvADAmount);
        btnDepositMoney = findViewById(R.id.btnDepositMoney);
        btnTransferMoney = findViewById(R.id.btnTransferMoney);
        btnWithdrawMoney = findViewById(R.id.btnWithdrawMoney);
        btnTransferMoney.setOnClickListener(this);
        btnDepositMoney.setOnClickListener(this);
        btnWithdrawMoney.setOnClickListener(this);

    }

    //Graps the intent send from the MainActivity and binds the document collections
    private void getIncomingIntent(){

        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("accountID") && getIntent().hasExtra("accountPath")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String idForAccount = getIntent().getStringExtra("accountID");
            pathForAccount = getIntent().getStringExtra("accountPath");
            String nameForAccount = getIntent().getStringExtra("accountName");
            accName = nameForAccount;
            accountID = idForAccount;

            Log.d(TAG, "getIncomingIntent: accountID: " + idForAccount);
            Log.d(TAG, "getIncomingIntent: accountPath: " + pathForAccount);
            Log.d(TAG, "getIncomingIntent: accountName: " + nameForAccount);
        } else {
            Log.d(TAG, "getIncomingIntent: No intent was had");
        }
    }

    /*
        sets the text on the activity acording to the data on firestore
     */
    private void loadDataFromFirestore(){
        Log.d(TAG, "setDataFromFirestore: " + FirebaseAuth.getInstance().getCurrentUser());
        Log.d(TAG, "loadDataFromFirestore: called account id with: " + accountID);


        db.collection("users").document(userId).collection("accounts").document(accountID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String name = documentSnapshot.getString("aName");
                    String type = documentSnapshot.getString("aType");
                    balance = documentSnapshot.getDouble("aAmount");
                    tvAccountName.setText(name);
                    tvAccountType.setText(type);
                    accountType = type;
                    tvAccountBalance.setText(Double.toString(balance) + "kr");
                    DocumentID = documentSnapshot.getId();



                } else {
                    Toast.makeText(AccountDetails.this,"Error loading account details", Toast.LENGTH_LONG).show();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }



    //sends a query to the Firestore based of the accountTransRef, and order it by timestamp time
    private void setUpRecyclerView() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference accountTransRef = db.collection("users").document(userId)
                .collection("accounts").document(accountID).collection("transactions");

        Query query = accountTransRef.orderBy("tTimestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<AccountTransactionModel> options = new FirestoreRecyclerOptions.Builder<AccountTransactionModel>()
                .setQuery(query, AccountTransactionModel.class)
                .build();

        adapter = new AccountTransferAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.rwTransactionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setTitle(){
        String accNameForTitle;
        accNameForTitle = accName;
        this.setTitle("Details for account - " + accNameForTitle);

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

    private void getWithdrawMoney(){
            DocumentReference userRef = db.collection("users").document(userId);
            DocumentReference accountRef = db.collection("users").document(userId).collection("accounts").document(accountID);

            final Task<DocumentSnapshot> taskUserRef = userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.i(TAG, "onSuccess: taskUserRef: Got Document");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: taskUserRef: Fail to get Document");
                }
            });

            final Task<DocumentSnapshot> taskAccuntRef = accountRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.i(TAG, "onSuccess: tastAccuntRef: Got Document");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: tastAccountRef: Fail to get Document");
                }
            });

        Tasks.whenAllComplete(taskAccuntRef,taskUserRef).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                long userAge = taskUserRef.getResult().getLong("uAge");
                final double accountBalance = taskAccuntRef.getResult().getDouble("aAmount");
                String accountType = taskAccuntRef.getResult().getString("aType");

                if (accountType.equals("pension")){
                    if (userAge >= 70){
                        Log.i(TAG, "onComplete: user age OK");

                        final EditText amountInputField = new EditText(AccountDetails.this);
                        amountInputField.setInputType(InputType.TYPE_CLASS_NUMBER);

                        AlertDialog dialog = new AlertDialog.Builder(AccountDetails.this)
                                .setTitle("Withdraw Money")
                                .setMessage("Enter amount to withdraw")
                                .setView(amountInputField)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i(TAG, "onClick: OK");
                                        if (accountBalance < Double.parseDouble(amountInputField.getText().toString())){
                                            Log.d(TAG, "onClick: amountError: No Cash");
                                            Toast.makeText(AccountDetails.this, "No Cash", Toast.LENGTH_SHORT).show();
                                        } else {
                                            transferMoney(amountInputField.getText().toString(), accountBalance);
                                        }
                                        
                                    }
                                }).setNegativeButton("Cancel",null)
                                .create();
                        dialog.show();

                    } else {
                        Log.i(TAG, "onComplete: user age to low");
                        Toast.makeText(AccountDetails.this, getString(R.string.withdraw_error_age), Toast.LENGTH_LONG).show();
                    }
                    
                } else {
                    Log.i(TAG, "onComplete: withdraw ok!");
                    final EditText amountInputField = new EditText(AccountDetails.this);
                    amountInputField.setInputType(InputType.TYPE_CLASS_NUMBER);

                    AlertDialog dialog = new AlertDialog.Builder(AccountDetails.this)
                            .setTitle("Withdraw Money")
                            .setMessage("Enter amount to withdraw")
                            .setView(amountInputField)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "onClick: OK");
                                    if (accountBalance < Double.parseDouble(amountInputField.getText().toString())){
                                        Log.d(TAG, "onClick: amountError: No Cash");
                                        Toast.makeText(AccountDetails.this, "No Cash", Toast.LENGTH_SHORT).show();
                                    } else {
                                        transferMoney(amountInputField.getText().toString(), accountBalance);
                                    }

                                }
                            }).setNegativeButton("Cancel",null)
                            .create();
                    dialog.show();

                }

            }
        });
    }
    private void transferMoney(String amount, double oldBalance){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference accountFromRef = db.collection("users").document(userId).collection("accounts").document(accountID);
        Log.d(TAG, "transferMoney: from account with id: " + accountID);

        final CollectionReference accountTransaction = db.collection("users").document(userId).collection("accounts").document(accountID)
                .collection("transactions");




        double valueFromET = Double.parseDouble(amount);
        final double newBalance = (oldBalance - valueFromET);


        final String tType = "withdraw";
        final Timestamp tTimestamp = Timestamp.now();
        final double tAmount = valueFromET;
        final String tDocumentId = accountID;
        final String tAccountToId = "";

        WriteBatch batch = db.batch();
        batch.update(accountFromRef,"aAmount",newBalance);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Finish money transfer");
                    accountTransaction.add(new AccountTransactionModel(tType,tAccountToId,tDocumentId,tTimestamp,tAmount)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AccountDetails.this, "Withdrawn some cash", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: Something went wrong" + task.getException());
                }
            }
        });
    }


    //Runs when activity loads, and actions happens
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called ");
        adapter.startListening();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

    }

    //runs when activity stops.
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called");
        adapter.stopListening();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    //Runs when activity is resumed.
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Called ");
        adapter.startListening();
        loadDataFromFirestore();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

    }

    /*
        OnClick switch cases
        uses the XML layout to know what button has been pressed
        requrest to implement View.onClickListener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDepositMoney:
                Log.d(TAG, "onClickSwichCase: clicked ");
                Intent intentDepo = new Intent(AccountDetails.this, PopActivity.class);
                intentDepo.putExtra("accountID", DocumentID);
                startActivity(intentDepo);
                break;
            case R.id.btnTransferMoney:
                Log.d(TAG, "onClickSwichCase: clicked ");
                Intent intentTransf = new Intent(AccountDetails.this, AccountTransfer.class);
                intentTransf.putExtra("accountID", DocumentID);
                intentTransf.putExtra("accountBalance",balance);
                startActivity(intentTransf);
                break;
            case R.id.btnWithdrawMoney:
                Log.i(TAG, "onClickSwtichCase withdraw: clicked ");
                getWithdrawMoney();
                break;

        }
    }
}
