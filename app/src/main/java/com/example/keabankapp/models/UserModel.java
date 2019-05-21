package com.example.keabankapp.models;

public class UserModel {
    private String uName;
    private String uLastName;
    private String uEmail;
    private int uPhone;
    private int uAge;
    private String uAdress;
    private int uZipCode;
    private String uFillial;

    public UserModel() {
    }

    public UserModel(String uName, String uLastName,int uAge, String uEmail, int uPhone, String uAdress, int uZipCode, String uFillial) {
        this.uName = uName;
        this.uLastName = uLastName;
        this.uAge = uAge;
        this.uEmail = uEmail;
        this.uPhone = uPhone;
        this.uAdress = uAdress;
        this.uZipCode = uZipCode;
        this.uFillial = uFillial;
    }

    public String getuName() {
        return uName;
    }

    public String getuLastName() {
        return uLastName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public int getuAge() {
        return uAge;
    }

    public int getuPhone() {
        return uPhone;
    }

    public String getuAdress() {
        return uAdress;
    }

    public int getuZipCode() {
        return uZipCode;
    }

    public String getuFillial() {
        return uFillial;
    }
}
