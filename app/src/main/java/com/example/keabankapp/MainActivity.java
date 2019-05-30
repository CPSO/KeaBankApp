package com.example.keabankapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.keabankapp.account.AccountCreate;
import com.example.keabankapp.account.AccountDetails;
import com.example.keabankapp.adapter.AccountAdapter;
import com.example.keabankapp.bill.BillListActivity;
import com.example.keabankapp.models.AccountModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //creates a referance to the collection in Firestore
    private CollectionReference accountsListRef = db.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("accounts");
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    private AccountAdapter adapter;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabOnClick);
        Log.d(TAG, "onCreate: Called");
        setUpRecyclerView();
        setupFirebaseAuth();
        try {
            autoPaymentGetList();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.account_page:
                Log.d(TAG, "onOptionsItemSelected: account_page");
                return true;
            case R.id.bill_page:
                Log.d(TAG, "onOptionsItemSelected: bill_page");
                Intent intent = new Intent(MainActivity.this, BillListActivity.class);
                startActivity(intent);
                return true;
            case R.id.card_page:
                Log.d(TAG, "onOptionsItemSelected: card_page");
                return true;
            case R.id.contact_page:
                Log.d(TAG, "onOptionsItemSelected: contact_page");
                return true;
            case R.id.user_page:
                Log.d(TAG, "onOptionsItemSelected: user_page");
                return true;
            case R.id.sign_out_page:
                Log.d(TAG, "onOptionsItemSelected: sign_out_page");
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //sends a query to the Firestore based of the courseListRef, and order it by courseName
    private void setUpRecyclerView() {
        Query query = accountsListRef.orderBy("aName", Query.Direction.ASCENDING);
        final FirestoreRecyclerOptions<AccountModel> options = new FirestoreRecyclerOptions.Builder<AccountModel>()
                .setQuery(query, AccountModel.class)
                .build();

        adapter = new AccountAdapter(options);

        //binding to the recycler view
        final RecyclerView recyclerView = findViewById(R.id.accounts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Sets click listener on the recyclerView.
        //gets the information on each element on the list
        //fills out the information to the intent and sends it to the CourseRatingActivity.
        adapter.setOnItemClickListener(new AccountAdapter.onItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {


                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String name = adapter.getItem(position).getaName();


                Toast.makeText(MainActivity.this,getString(R.string.toastSelectPosition) + position + getString(R.string.toastSelectID) + id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, AccountDetails.class);

                intent.putExtra("accountID", id);
                intent.putExtra("accountPath", path);
                intent.putExtra("accountName", name);
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "createAccount: pressed");
            Intent intent = new Intent(MainActivity.this, AccountCreate.class);
            startActivity(intent);
        }
    };

    private void autoPaymentGetList() throws ParseException {
      String userid =  FirebaseAuth.getInstance().getUid();

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        final Date todayWithZeroTime = formatter.parse(formatter.format(today));
        final Query payments = db.collection("users").document(userid).collection("payments").whereEqualTo("pIsPayed", false);
        payments.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: Found stuff");
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments() ){
                        Log.d(TAG, "onSuccess: Printing payment id(s): " + document.getId());
                        if (document.getTimestamp("pPayTime").toDate().equals(todayWithZeroTime)){
                            Log.d(TAG, "payTime is true " + document.getTimestamp("pPayTime").toDate());
                            try {
                                accountUpdate(document);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "payTime is false: Payment Day at:  " + document.getTimestamp("pPayTime").toDate().toString());
                            Log.d(TAG, "payTime is false: " + todayWithZeroTime);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Found no stuff");
            }
        });


    }

    private void accountUpdate(final DocumentSnapshot documentSnapshot) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        final Date todayWithZeroTime = formatter.parse(formatter.format(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        final Date addedMonth = calendar.getTime();
        Log.d(TAG, "accountUpdate: THIS IS THE NEW DATE " + addedMonth);




        String userid =  FirebaseAuth.getInstance().getUid();
        String documentID = documentSnapshot.getString("pAccountFromId");
        String paymentID = documentSnapshot.getId();
        Log.d(TAG, "accountUpdate: " + documentSnapshot.getString("pAccountFromId"));
        final double paymentAmount = documentSnapshot.getDouble("pAmount");
        final DocumentReference accountRef = db.collection("users").document(userid).collection("accounts").document(documentID);
        final DocumentReference paymentRef = db.collection("users").document(userid).collection("payments").document(paymentID);
        final WriteBatch batch = db.batch();
        accountRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot accountDoc) {
                if (accountDoc.exists()){
                    double oldBalance = accountDoc.getDouble("aAmount");
                    double newBalance = (oldBalance - paymentAmount);

                    batch.update(accountRef,"aAmount",newBalance);
                    batch.update(paymentRef,"pIsPayed",true);
                    if (documentSnapshot.getBoolean("pAutoPayment")){
                        batch.update(paymentRef,"pPayTime",addedMonth);
                        batch.update(paymentRef,"pIsPayed",false);
                        Log.d(TAG, "onSuccess: AutoPayment Detected ");
                        Log.d(TAG, "onSuccess: setting new month and payed to false");
                    }
                    batch.commit();
                    Log.d(TAG, "onSuccess: Commit Done");

                } else {
                    Log.d(TAG, "onSuccess: No File found");
                }
            }
        });


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
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }



    //signOut method
    //Uses the firebase Auth signOut function
    private void signOut(){
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
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
}
