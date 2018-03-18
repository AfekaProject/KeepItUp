package com.afeka.keepitup.keepitup;

import com.afeka.keepitup.keepitup.Transaction;

import java.util.Date;

public class TransactionAdapter {
    private int id;
    private String name;
    private Transaction.TransactionType type;
    private String company;
    private Date startDate;

    //optional parameters
    private Date endDate;
    private String notes;
    private double price;
    private Transaction.ChargeType chargeType;
    private Transaction.ForwardNotification notification;

    public TransactionAdapter(){

    }

    public TransactionAdapter(Transaction other) {
        this.id = other.getId();
        this.name = other.getName();
        this.type = other.getType();
        this.company = other.getCompany();
        this.price = other.getPrice();
        this.startDate = other.getStartDate();
        this.endDate = other.getEndDate();
        this.notes = other.getNotes();
        this.chargeType = other.getChargeType();
        this.notification = other.getNotification();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Transaction.TransactionType getType() {
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

    public String getNotes() {
        return notes;
    }

    public double getPrice() {
        return price;
    }

    public Transaction.ChargeType getChargeType() {
        return chargeType;
    }

    public Transaction.ForwardNotification getNotification() {
        return notification;
    }

}
