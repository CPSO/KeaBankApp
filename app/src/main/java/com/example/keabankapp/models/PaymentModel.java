package com.example.keabankapp.models;


import com.google.firebase.Timestamp;

import java.util.Date;

public class PaymentModel {
    String pTitle,pAccountFromId,pAccountToId;
    double pAmount;
    Timestamp pPaymentMade;
    Date pPayTime;
    boolean pAutoPayment, pIsPayed;

    public PaymentModel() {
    }

    public PaymentModel(String pTitle, String pAccountFromId, String pAccountToId, double pAmount, Date pPayTime, Timestamp pPaymentMade, boolean pAutoPayment, boolean pIsPayed) {
        this.pTitle = pTitle;
        this.pAccountFromId = pAccountFromId;
        this.pAccountToId = pAccountToId;
        this.pAmount = pAmount;
        this.pPayTime = pPayTime;
        this.pPaymentMade = pPaymentMade;
        this.pAutoPayment = pAutoPayment;
        this.pIsPayed = pIsPayed;
    }

    public String getpTitle() {
        return pTitle;
    }

    public String getpAccountFromId() {
        return pAccountFromId;
    }

    public String getpAccountToId() {
        return pAccountToId;
    }

    public double getpAmount() {
        return pAmount;
    }

    public Date getpPayTime() {
        return pPayTime;
    }

    public Timestamp getpPaymentMade() {
        return pPaymentMade;
    }

    public boolean ispAutoPayment() {
        return pAutoPayment;
    }

    public boolean ispIsPayed() {
        return pIsPayed;
    }
}
