package com.afeka.keepitup.keepitup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class Transaction {

    public enum  TransactionType {Insurance,Warranty , Provider}
    public enum ChargeType {None, Cash, CreditCard, BankCheck, StandingOrder}
    public enum ForwardNotification {Never, OneDay , TwoDays , TreeDays , Week}


    //required parameters
    private int id;
    private String name;
    private TransactionType type;
    private String company;
    private Date startDate;

    //optional parameters
    private Date endDate;
    private ArrayList<Bitmap> documents;
    private String notes;
    private double price;
    private ChargeType chargeType;
    private ForwardNotification notification;

    public Transaction(){

    }

    public Transaction(TransactionAdapter ta){
        this.id = ta.getId();
        this.name = ta.getName();
        this.type = ta.getType();
        this.company = ta.getCompany();
        this.startDate = ta.getStartDate();
        this.endDate = ta.getEndDate();
        this.notes = ta.getNotes();
        this.price = ta.getPrice();
        setDocumentsFromString(ta.getDocuments());
        setChargeType(ta.getChargeType());
        setNotification(ta.getNotification());
    }

    private Transaction(TransactionBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.company = builder.company;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.documents = builder.documents;
        this.notes = builder.notes;
        this.price = builder.price;
        setChargeType(builder.chargeType);
        setNotification(builder.notification);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChargeType(ChargeType chargeType) {
        if (chargeType == null)
            this.chargeType = ChargeType.None;
        else
            this.chargeType = chargeType;
    }

    public void setNotification(ForwardNotification notification) {
        if (notification == null)
                this.notification = ForwardNotification.Never;
        else
            this.notification = notification;
    }

    public void setDocuments(ArrayList<Bitmap> documents) {
        this.documents = documents;
    }

    private void setDocumentsFromString(ArrayList<String> documents) {
        this.documents = new ArrayList<>();
        if (documents!=null){
            for (int i = 0 ; i<documents.size();i++){
                byte[] decodedString = Base64.decode(documents.get(i), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                this.documents.add(decodedByte);
            }
        }
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

    public ArrayList<Bitmap> getDocuments() {
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
       Date currentDate = Calendar.getInstance().getTime();

        if(endDate == null)
            return false;
        else{
            if(endDate.getTime() - currentDate.getTime() > 0)
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
        private ArrayList<Bitmap> documents;
        private String notes;
        private double price;
        private ChargeType chargeType;
        private ForwardNotification notification;

        public TransactionBuilder (String name, TransactionType type,
                                   String company, Date startDate){
            this.name = name;
            this.type = type;
            this.company = company;
            this.startDate = startDate;
        }

        public TransactionBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder setEndDate(Date endDate){
            this.endDate = endDate;
            return this;
        }
        public TransactionBuilder setDocuments (ArrayList<Bitmap> documents){
            this.documents = documents;
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

    static final Comparator<Transaction> BY_NAME =
            new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase());
                }

            };

    static final Comparator<Transaction> BY_START_DATE =
            new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t1.getStartDate().compareTo(t2.getStartDate());
                }

            };
    static final Comparator<Transaction> BY_ID =
            new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t2.getId() - t1.getId();
                }

            };




}
