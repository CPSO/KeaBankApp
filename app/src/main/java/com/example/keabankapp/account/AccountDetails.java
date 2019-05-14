package com.example.keabankapp.account;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.MainActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


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
    private TextView tvAccountName, tvAccountType, tvAccountBalance;
    private String DocumentID;
    Button btnDepositMoney, btnTransferMoney;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        Log.d(TAG, "onCreate: called");
        getIncomingIntent();
        setupFirebaseAuth();
        setTitle();
        loadDataFromFirestore();
        tvAccountName = findViewById(R.id.tvADName);
        tvAccountType = findViewById(R.id.tvADType);
        tvAccountBalance = findViewById(R.id.tvADAmount);
        btnDepositMoney = findViewById(R.id.btnDepositMoney);
        btnTransferMoney = findViewById(R.id.btnTransferMoney);
        btnTransferMoney.setOnClickListener(this);
        btnDepositMoney.setOnClickListener(this);
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

    private void loadDataFromFirestore(){
        Log.d(TAG, "setDataFromFirestore: " + FirebaseAuth.getInstance().getCurrentUser());
        Log.d(TAG, "loadDataFromFirestore: called account id with: " + accountID);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection(userId).document("accounts").collection("accounts").document(accountID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String name = documentSnapshot.getString("aName");
                    String type = documentSnapshot.getString("aType");
                    double balance = documentSnapshot.getDouble("aAmount");
                    tvAccountName.setText(name);
                    tvAccountType.setText(type);
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


    private void setTitle(){
        String accNameForTitle;
        accNameForTitle = accName;
        this.setTitle("Details for account - " + accNameForTitle);

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
                    Intent intent = new Intent(AccountDetails.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
/*
    private View.OnClickListener onClickDepositMoney = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickDeposit: called ");
            Intent intent = new Intent(AccountDetails.this, PopActivity.class);
            intent.putExtra("accountID", DocumentID);
            startActivity(intent);
        }
    };
    */


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

    //Runs when activity is resumed.
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Called ");
        loadDataFromFirestore();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }


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
                startActivity(intentTransf);
                break;
        }
    }
}
