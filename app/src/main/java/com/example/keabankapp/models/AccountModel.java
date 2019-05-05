package com.example.keabankapp.models;

public class AccountModel {
    private String aName;
    private double aAmount;

    public AccountModel(String aName, double aAmount) {
        this.aName = aName;
        this.aAmount = aAmount;
    }

    public AccountModel() {
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public double getaAmount() {
        return aAmount;
    }

    public void setaAmout(double aAmout) {
        this.aAmount = aAmout;
    }
}
