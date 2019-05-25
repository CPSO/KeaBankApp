package com.example.keabankapp.account;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.ValueIterator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountTransactionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.Objects;


public class PopActivity extends Activity {
    Button btnSubmit;
    EditText etAmount;
    private String accountID;
    private static final String TAG = "PopActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private double currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        btnSubmit = findViewById(R.id.btnDepositConfirm);
        etAmount = findViewById(R.id.etDepositAmount);
        btnSubmit.setOnClickListener(OnClickListenerSubmit);
        setupFirebaseAuth();
        loadDataFromFirestore();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }

    private void loadDataFromFirestore(){
        accountID = getIntent().getStringExtra("accountID");
        Log.d(TAG, "setDataFromFirestore: " + FirebaseAuth.getInstance().getCurrentUser());
        Log.d(TAG, "loadDataFromFirestore: called account id with: " + accountID);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("accounts").document(accountID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Log.d(TAG, "onSuccess: Called");
                    currentBalance = documentSnapshot.getDouble("aAmount");
                } else {
                    Toast.makeText(PopActivity.this,"Error loading account details", Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error in document/firebase");
                    }
                });
    }

    public void updateBalance(){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference accountRef = db.collection("users").document(userId).collection("accounts").document(accountID);
        final CollectionReference accountTransaction = db.collection("users").document(userId).collection("accounts").document(accountID)
                                                .collection("transactions");


        double value;
        double valueInserted;
        String text = etAmount.getText().toString();
        try {
            value = currentBalance + Double.parseDouble(text);
            valueInserted = Double.parseDouble(text);

            Log.d(TAG, "onClickSubmit: trying parse double " + value);

            final String tType = "deposit";
            final Timestamp tTimestamp = Timestamp.now();
            final double tAmount = valueInserted;
            final String tDocumentId = accountID;
            final String tAccountToId = "";
            accountRef.update(
                    "aAmount",value).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull final Task<Void> task) {
                    if(task.isSuccessful()){
                        accountTransaction.add(new AccountTransactionModel(tType,tAccountToId,tDocumentId,tTimestamp,tAmount)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: Added Transaction History");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: History Failed to be added");
                            }
                        });
                        Toast.makeText(PopActivity.this, "Balance Added",
                                Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(PopActivity.this, "Failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e1){
            e1.printStackTrace();
            Log.d(TAG, "onClickSubmit: parsing failed ");
        }



    }


    private View.OnClickListener OnClickListenerSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickSubmit: called");
            updateBalance();
        }
    };


    //Checks the state of the user that is sign in.
    //ether the user is active or is signing out
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());


                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(PopActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
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
