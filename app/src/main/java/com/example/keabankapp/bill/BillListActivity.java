package com.example.keabankapp.bill;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.keabankapp.LoginActivity;
import com.example.keabankapp.R;
import com.example.keabankapp.adapter.AccountBillAdapter;
import com.example.keabankapp.models.PaymentModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class BillListActivity extends AppCompatActivity {
    private static final String TAG = "BillListActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AccountBillAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
        setContentView(R.layout.activity_bill_list);
        setUpRecyclerView();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(onClickFab);

    }
    /*
        @setUpRecyclerView
        Takes a Quary to make a list of documents that is displayed on the activity
        Uses the Payment Model and AcountBillAdapter
        An Adapter object acts as a bridge between an AdapterView and the underlying data for that view.
        The Adapter provides access to the data items. The Adapter is also responsible for making a View for each item in the data set.
     */
    private void setUpRecyclerView() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference accountBillsRef = db.collection("users").document(userId)
                .collection("payments");


        Query query = accountBillsRef.whereEqualTo("pAutoPayment",true);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: where:" + queryDocumentSnapshots);
            }
        });
        FirestoreRecyclerOptions<PaymentModel> options = new FirestoreRecyclerOptions.Builder<PaymentModel>()
                .setQuery(query, PaymentModel.class)
                .build();

        adapter = new AccountBillAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.auto_payment_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Intent intent = new Intent(BillListActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            }
        };
    }
    //Sets a listener on floating Action Button/fab and displays a snackbar
    private View.OnClickListener onClickFab = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BillListActivity.this, BillPaymentActivity.class);
            startActivity(intent);
        }
    };
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
