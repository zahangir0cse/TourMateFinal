package com.android.zsm.tourmatefinal;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.adapter.WeatherTabAdapter;
import com.android.zsm.tourmatefinal.preference.LocationPreference;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherInfo extends AppCompatActivity {
    public static final String OWM_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static  final  String IMAGE_PATH = "http://openweathermap.org/img/w/";
    public String unit ;
    public int foreCastCount ;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    public double lat ;
    public   double lon ;
    public  String mess;
    private Geocoder geocoder;
    FirebaseUser user;
    private FirebaseAuth auth;
    public  String cityName = null;
   private static FragmentManager fragmentManager;
   LocationPreference locationPreference;
   Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);
        locationPreference = new LocationPreference(this);
        String latitute= locationPreference.getLastSaveLatitute();
        String longitute = locationPreference.getLastSaveLongitute();
        if(latitute != null) {
            lat= Double.parseDouble(latitute) ;
        } else {
             lat = 23.777176;
        }
        if(longitute != null) {
            lon= Double.parseDouble(longitute) ;
        } else {
            lon = 90.399452;
        }

        client = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Intent intent = getIntent();
        if(intent.getStringExtra("unit")!=null){
            unit = intent.getStringExtra("unit");
        } else {
            unit = "metric";
        }
        if(intent.getIntExtra("foreCastCount",5)!= 5){
            foreCastCount = intent.getIntExtra("foreCastCount",5);
        } else {
            foreCastCount = 5;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Weather Information");
        setSupportActionBar(toolbar);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             cityName = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(this,
                            CityNameSuggestions.AUTHORITY,
                            CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(cityName, null);
            Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
           getLatLonByCity(cityName);
        } else {
            getCurrentLocation();
        }

        bundle = new Bundle();
        bundle.putDouble("lat", lat );
        bundle.putDouble("lon", lon );
        bundle.putString("unit", unit );
        bundle.putString("cityname", cityName );
        bundle.putInt("foreCastCount", foreCastCount );
        bundle.putString("mess",mess);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Curent Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Forecast Weather"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final WeatherTabAdapter adapter = new WeatherTabAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),bundle);
        //viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.getCurrentItem();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public  void  getLatLonByCity( String city) {

        if(Geocoder.isPresent()){
            try {
              //  String location = "theNameOfTheLocation";
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                List<Address> addresses= gc.getFromLocationName( city, 1); // get the found Address Objects
               if(addresses.size() >0) {
                if(addresses.get(0).hasLatitude() && addresses.get(0).hasLongitude()) {
                    lat = addresses.get(0).getLatitude();
                    lon = addresses.get(0).getLongitude();
                                  }
                } else {
                   mess = "Invalid city name";
               }
            } catch (IOException e) {
                Toast.makeText(WeatherInfo.this ,"size:----  "+e.getMessage() ,Toast.LENGTH_LONG).show();

                // handle the exception
            }
        }

    }
    private void getCurrentLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    lastLocation = task.getResult();
                    lat= lastLocation.getLatitude();
                    lon= lastLocation.getLongitude();

                  //  Toast.makeText(WeatherInfo.this ,"size:----  "+lastLocation.getLatitude() ,Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
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

    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        private int currentPage;

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
        }

        public final int getCurrentPage() {
            return currentPage;
        }
    }
}
