package com.android.zsm.tourmatefinal.preference;

import android.content.Context;
import android.content.SharedPreferences;


public class LocationPreference {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String LOCATION_PREFFERENCE_NAME = "takeTourLocation";

    public LocationPreference(Context context) {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(LOCATION_PREFFERENCE_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }
    public void saveLocation(String lat, String lon){
        editor.putString("lat",lat);
        editor.putString("lon",lon);
        editor.commit();
    }
    public String getLastSaveLatitute(){
        return sharedPreferences.getString("lat",null);
    }
    public String getLastSaveLongitute(){
        return  sharedPreferences.getString("lon",null);
    }
    public void resetLocation(){
        editor.clear();
        editor.commit();
    }
}
