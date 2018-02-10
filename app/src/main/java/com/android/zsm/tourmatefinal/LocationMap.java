package com.android.zsm.tourmatefinal;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.zsm.tourmatefinal.model.MarkerItem;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.clustering.ClusterManager;
import java.util.ArrayList;
import java.util.List;

public class LocationMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private List<MarkerItem> items = new ArrayList<>();
    private ClusterManager<MarkerItem> clusterManager;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbarLocation);
        toolbar.setTitle("Map");
        setSupportActionBar(toolbar);
        lat = Utility.getLatitute(this);
        lon = Utility.getLongitute(this);
        geoDataClient = Places.getGeoDataClient(this, null);
        placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        client = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainerLocation, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
    }

    private void getLastLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastLocation = task.getResult();
                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    if (latLng != null) {
                        map.addMarker(new MarkerOptions().title("My Current Place").position(latLng));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    } else {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 14));
                        map.addMarker(new MarkerOptions().title("My last Place").position(new LatLng(lat, lon)));
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
        clusterManager = new ClusterManager<>(this, map);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                items.add(new MarkerItem(latLng));
                clusterManager.addItems(items);
                clusterManager.cluster();
            }
        });
    }

    public void findCurrentPlaces(final View view) {
        checkPermission();
        placeDetectionClient.getCurrentPlace(null).addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    PlaceLikelihoodBufferResponse responses = task.getResult();
                    int count = responses.getCount();

                    String[] names = new String[count];
                    String[] addresses = new String[count];
                    LatLng[] latLngs = new LatLng[count];

                    for (int i = 0; i < count; i++) {
                        PlaceLikelihood likelihood = responses.get(i);
                        names[i] = (String) likelihood.getPlace().getName();
                        addresses[i] = (String) likelihood.getPlace().getAddress();
                        latLngs[i] = likelihood.getPlace().getLatLng();
                    }
                    responses.release();
                    openDialog(names, addresses, latLngs);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationMap.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDialog(final String[] names, final String[] addresses, final LatLng[] latLngs) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng latLng = latLngs[which];
                String address = addresses[which];
                String title = names[which];

                map.clear();
                map.addMarker(new MarkerOptions().position(latLng)
                        .title(title)
                        .snippet(address));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        };
        new AlertDialog.Builder(this).setTitle("Pick a place").setItems(names, listener).show();
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
