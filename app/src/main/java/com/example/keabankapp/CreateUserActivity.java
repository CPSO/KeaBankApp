package com.example.keabankapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keabankapp.models.AccountModel;
import com.example.keabankapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class CreateUserActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Log TAG
    EditText uFName, uLName, uMail, uPhoneNumber, uPassword, uUserAge;
    EditText uHomeAddress, uAddressZipcode;
    TextView bankLocCPH, bankLocOds;
    Button btnCreateUser;
    String uSelectedFilial;
    private static final String TAG = "CreateUserActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Log.d(TAG, "onCreate: Called");
        init();
        btnCreateUser.setOnClickListener(onClickCreateUser);
        mAuth = FirebaseAuth.getInstance();

    }

    private View.OnClickListener onClickCreateUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClickCreateUser: Called ");
            if (!validateForm()) {
                return;
            }
            createUser(uMail.getText().toString(),uPassword.getText().toString());
            //Intent intent = new Intent(CreateUserActivity.this, LoginActivity.class);
            //startActivity(intent);

        }
    };

    private void createUser(final String email, String password) {

        Log.d(TAG, "createUser:" + email);
        if (!validateForm()) {
            return;
        }
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            createUserDetails();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateUserActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void createUserDetails(){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        final DocumentReference userDetails = db.collection("users").document(userId);
        DocumentReference accountRefDef = db.collection("users").document(userId)
                .collection("accounts").document();
        DocumentReference accountRefBud = db.collection("users").document(userId)
                .collection("accounts").document();


        String aName = "Default account";
        String aName2 = "Budget account";
        double aAmount = 0.00;
        String aType = "default";
        String aType2 = "budget";
        final String uName = uFName.getText().toString();
        final String uLastName = uLName.getText().toString();
        final int uAge = Integer.parseInt(uUserAge.getText().toString());
        final String uEmail = uMail.getText().toString();
        final int uPhone = Integer.parseInt(uPhoneNumber.getText().toString());
        final String uAddress = uHomeAddress.getText().toString();
        final int uZipCode = Integer.parseInt(uAddressZipcode.getText().toString());
        final String uFillial = uSelectedFilial;

        WriteBatch batch = db.batch();

        batch.set(userDetails,new UserModel(uName,uLastName,uAge,uEmail,uPhone,uAddress,uZipCode,uFillial));
        batch.set(accountRefBud,new AccountModel(aName,aAmount,aType));
        batch.set(accountRefDef,new AccountModel(aName2,aAmount,aType2));

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: batch.commit() is a success");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: batch.commit is a failure");
            }
        });

    }

    private void setUserFilial(){
        int zipValue = Integer.parseInt(uAddressZipcode.getText().toString());
        if (zipValue < 5000){
            uSelectedFilial = "København";
            Log.d(TAG, "setUserFilial: " + uSelectedFilial);
        }
        if (zipValue >= 5000 && zipValue < 5999){
            uSelectedFilial = "Odense";
            Log.d(TAG, "setUserFilial: " + uSelectedFilial);
        } if (zipValue >= 6000 && zipValue < 9999){
            uSelectedFilial = "Århus";
            Log.d(TAG, "setUserFilial: "+ uSelectedFilial);
        }

    }


    private boolean validateForm() {
        boolean valid = true;

        String email = uMail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            uMail.setError("Required.");
            valid = false;
        } else {
            uMail.setError(null);
        }

        String password = uPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            uPassword.setError("Required.");
            valid = false;
        } else {
            uPassword.setError(null);
        }
        String userName = uFName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            uFName.setError("Required");
            valid = false;
        } else {
            uFName.setError(null);
        }
        String userLName = uLName.getText().toString();
        if (TextUtils.isEmpty(userLName)) {
            uLName.setError("Required");
            valid = false;
        } else {
            uLName.setError(null);
        }
        String userAge = uUserAge.getText().toString();
        if (TextUtils.isEmpty(userAge)) {
            uUserAge.setError("Required");
            valid = false;
        } else {
            uUserAge.setError(null);
        }
        String userEmail = uMail.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            uMail.setError("Required");
            valid = false;
        } else {
            uMail.setError(null);
        }
        String userPhone = uPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(userPhone)) {
            uPhoneNumber.setError("Required");
            valid = false;
        } else {
            uPhoneNumber.setError(null);
        }
        String userAddress = uHomeAddress.getText().toString();
        if (TextUtils.isEmpty(userAddress)) {
            uHomeAddress.setError("Required");
            valid = false;
        } else {
            uHomeAddress.setError(null);
        }
        String userAddressZipcode = uAddressZipcode.getText().toString();
        if (TextUtils.isEmpty(userAddressZipcode)) {
            uAddressZipcode.setError("Required");
            valid = false;
        } else {
            uAddressZipcode.setError(null);
            setUserFilial();
            
        }


        return valid;
    }





    private void init(){
        uFName = findViewById(R.id.etUserFName);
        uLName = findViewById(R.id.etUserLName);
        uMail = findViewById(R.id.etUserEmail);
        uPhoneNumber = findViewById(R.id.etUserPhone);
        uHomeAddress = findViewById(R.id.etAdressName);
        uAddressZipcode = findViewById(R.id.etZipCode);
        bankLocCPH = findViewById(R.id.tvCPH);
        bankLocOds = findViewById(R.id.tvOdense);
        btnCreateUser = findViewById(R.id.btnSignUp);
        uPassword = findViewById(R.id.etPassword);
        uUserAge = findViewById(R.id.etAge);

    }

    //Runs when activity loads, and actions happens
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called ");
        //FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);


    }
}