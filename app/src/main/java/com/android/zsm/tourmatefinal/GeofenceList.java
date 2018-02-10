package com.android.zsm.tourmatefinal;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.preference.LocationPreference;
import com.android.zsm.tourmatefinal.service.GeofencingPendingIntentService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeofenceList extends AppCompatActivity {

    private GeofencingClient client;
    private ArrayList<Geofence> geofences = new ArrayList<>();
    private PendingIntent pendingIntentObj;
    private LocationPreference locationPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_list);
        client = LocationServices.getGeofencingClient(this);
        pendingIntentObj = null;
        locationPreference = new LocationPreference(this);
        String latitute = locationPreference.getLastSaveLatitute();
        String longitute = locationPreference.getLastSaveLongitute();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(latitute), Double.valueOf(longitute), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        Geofence geofence = new Geofence.Builder()
                .setRequestId(stateName)
                .setCircularRegion(Double.valueOf(latitute), Double.valueOf(longitute), 200)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                //12 houre in mili second
                .setExpirationDuration(12 * 60 * 60 * 1000)
                .build();
        geofences.add(geofence);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    101);

            return;
        }
        client.addGeofences(getgeofencingRequest(), getGeofencingPendingIntentRequest() ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Geo fencing added",Toast.LENGTH_LONG).show();

            }
        });
    }

    private GeofencingRequest getgeofencingRequest() { GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencingPendingIntentRequest() {
        if (pendingIntentObj != null) {
            return pendingIntentObj;
        } else {
            Intent intent=new Intent(GeofenceList.this,GeofencingPendingIntentService.class);
            pendingIntentObj = PendingIntent.getService(this,
                    200,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntentObj;
        }
    }
}
