package com.android.zsm.tourmatefinal;

import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.zsm.tourmatefinal.model.MyPlaces;
import com.android.zsm.tourmatefinal.model.Results;
import com.android.zsm.tourmatefinal.service.IGoogleAPIService;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.zsm.tourmatefinal.utility.Utility.*;

public class NearByPlaceActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude, longitute;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;
    IGoogleAPIService mService;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_place);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapNear);
        mapFragment.getMapAsync(this);
        //Init Service
        mService = new Utility().getGoogleAPIService();
        //Request RuntimePermission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Code late
                switch (item.getItemId()) {
                    case R.id.action_hospital:
                        nearByPlace("hospital");
                        break;
                    case R.id.action_market:
                        nearByPlace("market");
                        break;
                    case R.id.action_restaurant:
                        nearByPlace("restaurant");
                        break;
                    case R.id.action_school:
                        nearByPlace("school");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void nearByPlace(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude, longitute, placeType);
        mService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {


                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeNmae = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeNmae);
                                if (placeType.endsWith("hospital"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_hospital));
                                else if (placeType.endsWith("market"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_market));
                                else if (placeType.endsWith("restaurant"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_restrnt));
                                else if (placeType.endsWith("school"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_school));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                //Add to map
                                mMap.addMarker(markerOptions);

                                //Move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                        Toast.makeText(NearByPlaceActivity.this, "LocationFailed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private String getUrl(double latitude, double longitute, String placeType) {
        StringBuilder googleplacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleplacesUrl.append("location="+latitude+ ","+longitute);
        googleplacesUrl.append("&radius="+ 1000);
        googleplacesUrl.append("&type="+placeType);
        googleplacesUrl.append("&sensor=true");
        googleplacesUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl", googleplacesUrl.toString());
        return googleplacesUrl.toString();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        } else
            return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length >=0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);

                    }
                } else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

            }
            break;
        }


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest =new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
    }





    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation =location;
        if (mMarker!=null){
            mMarker.remove();
            latitude=location.getLatitude();
            longitute=location.getLongitude();
            LatLng latLng=new LatLng(latitude,longitute);
            MarkerOptions markerOptions=new MarkerOptions()
                    .position(latLng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMarker=mMap.addMarker(markerOptions);
            //Mone camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


            if (mGoogleApiClient!=null){
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            }
        }
    }
}
