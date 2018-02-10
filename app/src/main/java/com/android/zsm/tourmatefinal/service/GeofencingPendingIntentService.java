package com.android.zsm.tourmatefinal.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.android.zsm.tourmatefinal.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.ArrayList;
import java.util.List;

public class GeofencingPendingIntentService extends IntentService {

    public GeofencingPendingIntentService() {
        super("GeofencingPendingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int transitionType = geofencingEvent.getGeofenceTransition();
        String transitionString = "";
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionString = "Entered : ";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionString = "Exited : ";
                break;
        }

        List<Geofence>triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        List<String>triggeringGeofenceIDs = new ArrayList<>();
        for(Geofence g : triggeringGeofences){
            triggeringGeofenceIDs.add(g.getRequestId());
        }

        String notificationString = transitionString+ TextUtils.join(", ",triggeringGeofenceIDs);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.add_icon);
        builder.setContentTitle(notificationString);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(55,builder.build());

    }


}
