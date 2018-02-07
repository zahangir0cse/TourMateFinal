package com.android.zsm.tourmatefinal.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by shamim on 1/24/2018.
 */

public class UserPreference {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String USER_PREF_NAME = "takeTourUser";

    public UserPreference(Context context) {
        this._context = context;
        preferences = _context.getSharedPreferences(USER_PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void saveUser(String uid, String email, boolean loginStatus) {
        editor.putString("uid", uid);
        editor.putString("email", email);
        editor.putBoolean("loginstatus", loginStatus);
        editor.commit();
    }

    public boolean getLoginstatus() {
        return preferences.getBoolean("loginstatus", false);
    }

    public String getUserEmail() {
        return preferences.getString("email", null);
    }

    public void resetUser() {
        editor.clear();
        editor.commit();
    }
}
