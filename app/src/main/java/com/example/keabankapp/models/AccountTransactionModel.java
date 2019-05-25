package com.example.keabankapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

public class AccountTransactionModel {
    private String tType,tAccountToId, tDocumentId;
    private Timestamp tTimestamp;
    private double tAmount;

    public AccountTransactionModel() {
    }

    public AccountTransactionModel(String tType, String tAccountToId, String tDocumentId, Timestamp tTimestamp, double tAmount) {
        this.tType = tType;
        this.tAccountToId = tAccountToId;
        this.tDocumentId = tDocumentId;
        this.tTimestamp = tTimestamp;
        this.tAmount = tAmount;
    }

    public String gettType() {
        return tType;
    }

    public String gettAccountToId() {
        return tAccountToId;
    }

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
