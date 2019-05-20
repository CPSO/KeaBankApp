package com.example.keabankapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class AccountTransactionModel {
    private String tType,tAccountToId, tDocumentId;
    private Timestamp tTimestamp;
    private double tAmount;

    public AccountTransactionModel() {
    }

    public String gettType() {
        return tType;
    }

    public String gettAccountToId() {
        return tAccountToId;
    }

    @Exclude
    public String gettDocumentId() {
        return tDocumentId;
    }

    public void settDocumentId(String tDocumentId) {
        this.tDocumentId = tDocumentId;
    }

    public Timestamp gettTimestamp() {
        return tTimestamp;
    }

    public double gettAmount() {
        return tAmount;
    }
}
