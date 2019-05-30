package com.example.keabankapp.models;

/*
    Cloud Firestore also supports writing your own Java objects with custom classes.
    Cloud Firestore will internally convert the objects to supported data types.
 */
public class AccountModel {
    private String aName;
    private double aAmount;
    private String aType;


    public AccountModel(String aName, double aAmount, String aType) {
        this.aName = aName;
        this.aAmount = aAmount;
        this.aType = aType;
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

    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }
}
