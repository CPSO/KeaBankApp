package com.example.keabankapp.bill;
//FIXME A list of a gameplan:
// # Make a PayNow method that pays the bills today. sets isPayed true and saves it in payments collection.
// - Make a Later Date payment, that saves the bill for a later date, sets isPlayed false.
// - Make a Mehod for MainActivity to all not paid bills and checks the date if they needs to be paid.


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.models.AccountTransactionModel;
import com.example.keabankapp.models.PaymentModel;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BillPaymentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //TAG
    private static final String TAG = "BillPaymentActivity";
    //Widgets
    private EditText paymentAmount,paymentName,accountReciver;
    //private DatePicker datePicker;
    private Spinner spinnerAccount;
    private TextView accountAmountTV,datePicker;
    private Button buttonSubmit;
    private CheckBox autoPayment;
    //Firebase/Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Variables
    private String userID;
    private String selectedAccountID;
    private String stringDate;
    private Date returnDate;
    private Double selectedAccountBalance;
    private boolean isAuto;
    private boolean isSameDate = false;
    private SparseIntArray nemCode = new SparseIntArray();
    private static final String TV_KEY = "";
    private String DATE_KEY = "";
    private static final Date SOME_DATE = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
        setContentView(R.layout.activity_bill_payment);
        setupNemCode();
        init();
    }

    private View.OnClickListener onClickDatePicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDatePickerDialog();
        }
    };

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog( this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String dateshow = dayOfMonth + "-" + month + "-" + year;
        stringDate = dayOfMonth + "-" + month + "-" + year;
        datePicker.setText(dateshow);
        getDateFromString(stringDate);
        Log.d(TAG, "onDateSet: " + getDateFromString(stringDate));

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        try {
            Date todayWithZeroTime = formatter.parse(formatter.format(today));
                if (getDateFromString(stringDate).equals(todayWithZeroTime)){
                    Log.d(TAG, "onDateSet: " + returnDate.toString() + todayWithZeroTime.toString());
                    Log.d(TAG, "onDateSet: SAME DATE");
                    isSameDate = true;
                    Log.d(TAG, "onDateSet: " + isSameDate);
                } else {
                    Log.d(TAG, "onDateSet: NOT SAME DATE");
                    isSameDate = false;
                    Log.d(TAG, "onDateSet: " + isSameDate);
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    public Date getDateFromString(String datetoSaved){
        try {
            returnDate = format.parse(datetoSaved);
            return returnDate;
        } catch (ParseException e){

            Log.d(TAG, "getDateFromString: " + e.toString());
            return null ;
        }

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


    private View.OnClickListener onClickSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!validateForm()) {
                return;
            }
            double payment = Double.parseDouble(paymentAmount.getText().toString());
            if (selectedAccountBalance < payment )
            {
                Log.d(TAG, "onClick: Balance Check: Low Balance!");
            } else {
                Log.d(TAG, "onClick: Balance Check: Balance OK!");
                    if (isSameDate){
                        Log.d(TAG, "IsSameDate = true " + isSameDate);
                        nemID();
                    } else {
                        Log.d(TAG, "IsSameDate = false " + isSameDate);
                        nemID();
                    }
            }
        }
    };

    private void payNow() {
        Log.d(TAG, "payNow: Called");
       final CollectionReference paymentRef = db.collection("users").document(userID).collection("payments");
        final DocumentReference accountBalanceRef = db.collection("users").document(userID).collection("accounts").document(selectedAccountID);
       final String pTitle = paymentName.getText().toString();
       final String pAccountFromId = selectedAccountID;
       final String pAccountToId = accountReciver.getText().toString();
       final double pAmount = Double.parseDouble(paymentAmount.getText().toString());
       final Date pPayTime = returnDate;
       final Timestamp pPaymentMade = Timestamp.now();
       final boolean pAutoPayment = isAuto;
       final boolean pIsPayed = true;


        accountBalanceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    selectedAccountBalance = documentSnapshot.getDouble("aAmount");
                    if (selectedAccountBalance < pAmount){
                        Log.d(TAG, "onSuccess: Amount is bigger than balance: " + selectedAccountBalance + " < " + pAmount);

                    } else {
                        Log.d(TAG, "onSuccess: Balance is OK to procede");
                        paymentRef.add(new PaymentModel(pTitle,pAccountFromId,pAccountToId,pAmount,pPayTime,pPaymentMade,pAutoPayment,pIsPayed)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: added payment");
                                double newBalance = (selectedAccountBalance - pAmount);
                                accountBalanceRef.update("aAmount",newBalance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: set a new balance");
                                        makeTransactionHistory();
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });

                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error getting accountBalanceRef");
            }
        });


        /*
            Tast 1 Remove Balance on selected account
            Tast 2 Add payment to DB
            Finish
         */

    }

    private void makeTransactionHistory(){
        Log.d(TAG, "makeTransactionHistory: Called");
        final CollectionReference accountTransactionFrom = db.collection("users").document(userID).collection("accounts").document(selectedAccountID)
                .collection("transactions");

        final String tType = "payment";
        final Timestamp tTimestamp = Timestamp.now();
        final double tAmount = Double.parseDouble(paymentAmount.getText().toString());;
        final String tDocumentId = selectedAccountID;
        final String tAccountToId = "";

        final Task<DocumentReference> addAccountTransfer = accountTransactionFrom.add(new AccountTransactionModel(tType,tAccountToId,tDocumentId,tTimestamp,tAmount)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: addAccountTransfer");
            }
        });

        Tasks.whenAllComplete(addAccountTransfer).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
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



    private void makePayment(){
        Log.d(TAG, "makePayment: Called");
        CollectionReference paymentRef = db.collection("users").document(userID).collection("payments");
        String pTitle = paymentName.getText().toString();
        String pAccountFromId = selectedAccountID;
        String pAccountToId = accountReciver.getText().toString();
        double pAmount = Double.parseDouble(paymentAmount.getText().toString());
        Date pPayTime = returnDate;
        Timestamp pPaymentMade = Timestamp.now();
        boolean pAutoPayment = isAuto;
        final boolean pIsPayed = false;

        paymentRef.add(new PaymentModel(pTitle,pAccountFromId,pAccountToId,pAmount,pPayTime,pPaymentMade,pAutoPayment,pIsPayed)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: MakePayment Called: " + pIsPayed);
                finish();
            }
        });



        }

    private CompoundButton.OnCheckedChangeListener checkedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Log.d(TAG, "onCheckedChanged: Checkbox is checked");
                isAuto = true;
            } else {
                Log.d(TAG, "onCheckedChanged: Checkbox is unChecked");
                isAuto = false;

            }
        }
    };


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
                            if (isSameDate = true){
                                Log.d(TAG, "onClick: NEMID Calls for PAYNOW");
                                payNow();
                            } else {
                                makePayment();
                                Log.d(TAG, "onClick: NEMID Calls for makePayment");

                            }

                            Toast.makeText(BillPaymentActivity.this,"Sending Money", Toast.LENGTH_LONG).show();


                        } else {
                            Log.d(TAG, "onClick: " + nemIDValue + " != " + selectedValueString);
                            Toast.makeText(BillPaymentActivity.this,"Wrong Value", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }






    private boolean validateForm() {
        boolean valid = true;

        String payAmount = paymentAmount.getText().toString();
        if (TextUtils.isEmpty(payAmount)) {
            paymentAmount.setError("Required.");
            valid = false;
        } else {
            paymentAmount.setError(null);
        }
        String date = datePicker.getText().toString();
        if (TextUtils.isEmpty(date)) {
            datePicker.setError("Required.");
            valid = false;
        } else  {
            datePicker.setError(null);
        }

        String payName = paymentName.getText().toString();
        if (TextUtils.isEmpty(payName)){
            paymentName.setError("Required.");
        } else {
            paymentName.setError(null);
        }

        String payAccountTo = accountReciver.getText().toString();
        if (TextUtils.isEmpty(payAccountTo)){
            accountReciver.setError("Required.");
        } else {
            accountReciver.setError(null);
        }
        return valid;
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

    private void setupNemCode(){
        //#Key - Value
        nemCode.put(1278,3298);
        nemCode.put(4565,9137);
        nemCode.put(8264,7304);
        nemCode.put(7615,6931);
    }
    private void init(){
        datePicker = findViewById(R.id.tvDatePicker);
        datePicker.setOnClickListener(onClickDatePicker);
        paymentAmount = findViewById(R.id.etPaymentAmount);
        paymentName = findViewById(R.id.etNameForPayment);
        accountReciver = findViewById(R.id.etAccountToInfo);
        spinnerAccount = findViewById(R.id.spinnerPaymentAccount);
        accountAmountTV = findViewById(R.id.tvSelectedAccountBalance);
        buttonSubmit = findViewById(R.id.btnSubmitPayment);
        buttonSubmit.setOnClickListener(onClickSubmit);
        autoPayment = findViewById(R.id.cbAutoPay);
        autoPayment.setOnCheckedChangeListener(checkedListener);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState called");
        outState.putString(DATE_KEY,stringDate);
        outState.putString("saveText",stringDate);
        Log.d(TAG, "onSaveInstanceState: " + stringDate);
        Log.d(TAG, "onSaveInstanceState: state of isSameDate:" + isSameDate);



    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstance() called with " + savedInstanceState.getString("saveText"));
        getDateFromString(savedInstanceState.getString("saveText"));
        Log.d(TAG, "onRestoreInstanceState: " + getDateFromString(savedInstanceState.getString("saveText")));
        Log.d(TAG, "onRestoreInstanceState: state of isSameDate " + isSameDate);
        stringDate = savedInstanceState.getString("saveText");
        Log.d(TAG, "onRestoreInstanceState: what is string text" + stringDate);
        datePicker.setText(savedInstanceState.getString(TV_KEY));
    }
}

