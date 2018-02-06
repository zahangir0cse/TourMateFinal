package com.android.zsm.tourmatefinal.model;

import java.io.Serializable;

/**
 * Created by Mobile App Develop on 1/25/2018.
 */

public class Events implements Serializable {
    String eventID;
    String userID;
    String eventName;
    double budget;
    String eventDate;
    String createDate;

    public Events() {
    }
    public Events(String eventID, double budget) {
        this.eventID=eventID;
        this.budget=budget;
    }
    public Events(String eventID, String userID, String eventName, double budget) {
        this.eventID=eventID;
        this.userID=userID;
        this.eventName=eventName;
        this.budget=budget;
    }

    public Events(String eventID, String userID, String eventName, double budget, String eventDate) {
        this.eventID=eventID;
        this.userID=userID;
        this.eventName=eventName;
        this.budget=budget;
        this.eventDate=eventDate;
    }

    public Events(String eventID, String userID, String eventName, double budget, String eventDate, String createDate) {
        this.eventID=eventID;
        this.userID=userID;
        this.eventName=eventName;
        this.budget=budget;
        this.eventDate=eventDate;
        this.createDate=createDate;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID=eventID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID=userID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName=eventName;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget=budget;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate=eventDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate=createDate;
    }
}
