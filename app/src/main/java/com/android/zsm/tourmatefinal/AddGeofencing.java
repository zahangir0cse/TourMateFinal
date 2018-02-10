package com.android.zsm.tourmatefinal;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.model.Events;
import com.android.zsm.tourmatefinal.model.Geofenc;
import com.android.zsm.tourmatefinal.service.GeofencingPendingIntentService;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddGeofencing extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private EditText geonameEt, geoRadiusEt;
    public double lat;
    public double lon;
    private ArrayList<Geofence> geofences = new ArrayList<>();
    private PendingIntent pendingIntent;
    private GeofencingClient geoclient;
    DatabaseReference root;
    FirebaseDatabase firebaseDatabase;
    private Events event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geofencing);
        Intent intent = getIntent();
        event = (Events) intent.getSerializableExtra("obj");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        geonameEt = findViewById(R.id.fenceName);
        geoRadiusEt = findViewById(R.id.rarius);
        Toolbar toolbar = findViewById(R.id.toolbarGeo);
        toolbar.setTitle("Map");
        setSupportActionBar(toolbar);
        geoDataClient = Places.getGeoDataClient(this, null);
        placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        client = LocationServices.getFusedLocationProviderClient(this);
        geoclient = LocationServices.getGeofencingClient(this);
        getLastLocation();
        root = firebaseDatabase.getInstance().getReference("Geofence");
        root.keepSynced(true);
    }

    private void getLastLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastLocation = task.getResult();
                    lat = lastLocation.getLatitude();
                    lon = lastLocation.getLongitude();
                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    if (latLng != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    }
                }
            }
        });
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        checkPermission();
        map.setMyLocationEnabled(true);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                lat = latLng.latitude;
                lon = latLng.longitude;
                map.addMarker(markerOptions);

            }
        });
    }

    public void createFencing(View view) {
        Float radias = Float.parseFloat(geoRadiusEt.getText().toString());
        String geoname = geonameEt.getText().toString();
        pendingIntent = null;

        Geofence geofence = new Geofence.Builder()
                .setRequestId(geoname)
                .setCircularRegion(lat, lon, radias)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(12 * 60 * 60 * 1000)
                .build();

        geofences.add(geofence);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        geoclient.addGeofences(getGeofencingRequest(), getGeofencingPendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddGeofencing.this, "geofence added", Toast.LENGTH_SHORT).show();
            }
        });


        String fencid = root.push().getKey();
        Geofenc ev = new Geofenc(fencid, event.getEventID(), geoname, lat, lon);
        root.child(fencid).setValue(ev);
        startActivity(new Intent(AddGeofencing.this, GeofenceList.class));
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencingPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        } else {
            Intent intent = new Intent(AddGeofencing.this, GeofencingPendingIntentService.class);

            pendingIntent = PendingIntent.getService(this,
                    200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new Utility().onCreateOptionsMenuUtil(menu, this, this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        new Utility().onPrepareOptionsMenuUtil(menu, user);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new Utility().onOptionSelectedUtil(item, this, this, this);
        return super.onOptionsItemSelected(item);
    }

}
