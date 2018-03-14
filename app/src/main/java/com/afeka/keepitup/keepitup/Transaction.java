package com.afeka.keepitup.keepitup;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Transaction {

    enum  TransactionType {Insurance,Warranty , Provider}
    enum ChargeType {Cash,CreditCard,BankCheck,StandingOrder}
    enum ForwardNotification {Never, OneDay , TwoDays , TreeDays , Week}

    //required parameters
    private int id;
    private String name;
    private TransactionType type;
    private String company;
    private Date startDate;

    //optional parameters
    private Date endDate;
    private ArrayList<File> documents;
    private String notes;
    private double price;
    private ChargeType chargeType;
    private ForwardNotification notification;

    private Transaction(TransactionBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.company = builder.company;
        this.startDate = builder.startDate;


    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCompany() {
        return company;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ArrayList<File> getDocuments() {
        return documents;
    }

    public String getNotes() {
        return notes;
    }

    public double getPrice() {
        return price;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public ForwardNotification getNotification() {
        return notification;
    }

    public Boolean isExpired(){
        if(endDate == null)
            return false;
        else{
            if(endDate.compareTo(Calendar.getInstance().getTime()) > 0)
                return false;
            else
                return true;
        }
    }

    public static class TransactionBuilder {
        //required parameters
        private int id;
        private String name;
        private TransactionType type;
        private String company;
        private Date startDate;

        //optional parameters
        private Date endDate;
        private ArrayList<File> documents;
        private String notes;
        private double price;
        private ChargeType chargeType;
        private ForwardNotification notification;



        public TransactionBuilder (int id, String name, TransactionType type,
                                   String company, Date startDate){
            this.id = id;
            this.name = name;
            this.type = type;
            this.company = company;
            this.startDate = startDate;
        }

        public TransactionBuilder setEndDate(Date endDate){
            this.endDate = endDate;
            return this;
        }

        public TransactionBuilder setNotes (String notes){
            this.notes = notes;
            return this;
        }
        public TransactionBuilder setPrice (double price){
            this.price = price;
            return this;
        }
        public TransactionBuilder setChargeType (ChargeType chargeType){
            this.chargeType = chargeType;
            return this;
        }
        public TransactionBuilder setNotification (ForwardNotification notification){
            this.notification = notification;
            return this;
        }

        public Transaction build(){
            return new Transaction(this);
        }




    }



}
