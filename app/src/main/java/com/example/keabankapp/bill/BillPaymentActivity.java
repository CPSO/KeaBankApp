package com.example.keabankapp.bill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.models.PaymentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private String date;
    private Double selectedAccountBalance;
    private boolean isAuto;
    private boolean isSameDate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
        setContentView(R.layout.activity_bill_payment);
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
        String dateshow = dayOfMonth + "/" + month + "/" + year;
        date = dayOfMonth + "-" + month + "-" + year;
        datePicker.setText(dateshow);
        getDateFromString(date);
        Log.d(TAG, "onDateSet: " + getDateFromString(date));

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        try {
            Date todayWithZeroTime = formatter.parse(formatter.format(today));
                if (getDateFromString(date).equals(todayWithZeroTime)){
                    Log.d(TAG, "onDateSet: SAME DATE");
                    isSameDate = true;
                } else {
                    Log.d(TAG, "onDateSet: NOT SAME DATE");
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    public Date getDateFromString(String datetoSaved){
        try {
            Date date = format.parse(datetoSaved);
            return date ;
        } catch (ParseException e){
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
                makePayment();
            }
        }
    };

    private void makePayment(){
        CollectionReference paymentRef = db.collection("users").document(userID).collection("payments");
        String pTitle = paymentName.getText().toString();
        String pAccountFromId = selectedAccountID;
        String pAccountToId = accountReciver.getText().toString();
        double pAmount = Double.parseDouble(paymentAmount.getText().toString());
        Date pPayTime = getDateFromString(date);
        Timestamp pPaymentMade = Timestamp.now();
        boolean pAutoPayment = isAuto;
        boolean pIsPayed = false;

        paymentRef.add(new PaymentModel(pTitle,pAccountFromId,pAccountToId,pAmount,pPayTime,pPaymentMade,pAutoPayment,pIsPayed)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: added payment");
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






    private boolean validateForm() {
        boolean valid = true;

        String payAmount = paymentAmount.getText().toString();
        if (TextUtils.isEmpty(payAmount)) {
            paymentAmount.setError("Required.");
            valid = false;
        } else {
            paymentAmount.setError(null);
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
}
