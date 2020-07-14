package com.expense.tracker.model;

public class Transaction {
    private double amount;
    private TransactionType transactionType;
    private String tag;
    private String date;

    public Transaction(){

    }
    public Transaction(double amount, TransactionType transactionType, String tag, String date) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.tag = tag;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
