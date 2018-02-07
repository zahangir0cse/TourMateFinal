package com.android.zsm.tourmatefinal.model;

public class User {
    String userID;
    String name;
    String email;
    String userPhoto;

    public User(String userID, String name, String email) {
        this.userID=userID;
        this.name=name;
        this.email=email;
    }

    public User(String userID, String name, String email, String userPhoto) {
        this.userID=userID;
        this.name=name;
        this.email=email;
        this.userPhoto=userPhoto;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID=userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto=userPhoto;
    }
}
