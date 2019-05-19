package com.example.keabankapp;

import android.content.Intent;
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

import com.example.keabankapp.account.AccountCreate;
import com.example.keabankapp.models.AccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    private static final int ERROR_DIALOG_REQUEST = 9001;
    EditText uFName, uLName, uMail, uPhone, uPassword;
    EditText uAdress, uZipCode;
    TextView bankLocCPH, bankLocOds;
    Button btnCreateUser;
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

    private void createUser(String email, String password) {

        Log.d(TAG, "createUser:" + email);
        if (!validateForm()) {
            return;
        }
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            String aName = "Default account";
                            String aName2 = "Budget account";
                            double aAmount = 0.00;
                            String aType = "default";
                            String aType2 = "budget";


                            DocumentReference accountRefDef = FirebaseFirestore.getInstance()
                                    .collection(userId).document("accounts").collection("accounts").document();
                            DocumentReference accountRefBud = FirebaseFirestore.getInstance()
                                    .collection(userId).document("accounts").collection("accounts").document();



                            accountRefDef.set(new AccountModel(aName,aAmount,aType));
                            accountRefBud.set(new AccountModel(aName2,aAmount,aType2));
                            finish();



                        } else {
                            // If sign in fails, display a message to the user.
                            String error = task.getException().toString();
                            String errorPrint = error.substring(error.lastIndexOf(":") + 1);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateUserActivity.this, errorPrint,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void createAccount (){


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

        return valid;
    }





    private void init(){
        uFName = findViewById(R.id.etUserFName);
        uLName = findViewById(R.id.etUserLName);
        uMail = findViewById(R.id.etUserEmail);
        uPhone = findViewById(R.id.etUserPhone);
        uAdress = findViewById(R.id.etAdressName);
        uZipCode = findViewById(R.id.etZipCode);
        bankLocCPH = findViewById(R.id.tvCPH);
        bankLocOds = findViewById(R.id.tvOdense);
        btnCreateUser = findViewById(R.id.btnSignUp);
        uPassword = findViewById(R.id.etPassword);

    }

    //Runs when activity loads, and actions happens
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called ");
        //FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);


    }
}