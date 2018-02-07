package com.android.zsm.tourmatefinal.model;

import java.io.Serializable;

public class Expenditure implements Serializable {
    private String expenseid;
    private String eventid;
    private String description;
    private double expense;
    private String createdate;

    public Expenditure() {
    }

    public Expenditure(String expenseid, String eventid, String description, double expense) {
        this.expenseid = expenseid;
        this.eventid = eventid;
        this.description = description;
        this.expense = expense;
    }

    public Expenditure(String expenseid, String eventid, String description, double expense, String createdate) {
        this.expenseid = expenseid;
        this.eventid = eventid;
        this.description = description;
        this.expense = expense;
        this.createdate = createdate;
    }

    public String getExpenseid() {
        return expenseid;
    }

    public void setExpenseid(String expenseid) {
        this.expenseid = expenseid;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }
}
