package com.android.zsm.tourmatefinal;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TakeTour extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}