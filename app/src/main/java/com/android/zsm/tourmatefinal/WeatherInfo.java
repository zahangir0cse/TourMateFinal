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
        String latitute= locationPreference.getLaetSaveLatitute();
        String longitute = locationPreference.getLaetSaveLongitute();
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Curent Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Forecast Weather"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
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
    /************************************** Menu Item Stsrt Here ************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem CelsiusItem = menu.findItem(R.id.tempc);
        MenuItem FahrenheitItem = menu.findItem(R.id.tempf);
        MenuItem EventItem = menu.findItem(R.id.events);
        MenuItem MapItem = menu.findItem(R.id.location_map);
        MenuItem NearPlaceItem = menu.findItem(R.id.nearplace);
        MenuItem MapDirectionItem = menu.findItem(R.id.direction);
        MenuItem WeatherItem = menu.findItem(R.id.weather_info);
        MenuItem LogoutItem = menu.findItem(R.id.logout);
        MenuItem Myprofile = menu.findItem(R.id.profile);
        CelsiusItem.setVisible(true);
        FahrenheitItem.setVisible(true);
        switch (unit) {
            case "metric":
                CelsiusItem.setVisible(false);
                FahrenheitItem.setVisible(true);
                break;
            case "imperial":
                CelsiusItem.setVisible(true);
                FahrenheitItem.setVisible(false);
                break;
        }

        EventItem.setVisible(true);
        MapItem.setVisible(true);
        NearPlaceItem.setVisible(true);
        MapDirectionItem.setVisible(true);
        WeatherItem.setVisible(true);
        if(user != null) {
            LogoutItem.setVisible(true);
            Myprofile.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hm:
                 startActivity(new Intent(WeatherInfo.this,EventList.class));
                break;
            case R.id.events:
                 startActivity(new Intent(WeatherInfo.this,EventList.class));
                break;
            case R.id.location_map:
                startActivity(new Intent(this,LocationMap.class));
                break;
            case R.id.nearplace:

                startActivity(new Intent(this,NearestPlace.class));
                break;
            case R.id.direction:
                startActivity(new Intent(this,DirectionMap.class));
                break;
            case R.id.weather_info:
               // startActivity(new Intent(this,WeatherInfo.class));
                break;
            case R.id.profile:
                 startActivity(new Intent(this,UserProfile.class));
                break;
            case R.id.tempc:
                 startActivity(new Intent(this,WeatherInfo.class).putExtra("unit","metric"));
                break;
            case R.id.tempf:
                 startActivity(new Intent(this,WeatherInfo.class).putExtra("unit","imperial"));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /************************************** Menu Item End Here ************************************/

    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (WeatherInfo.this,LoginActivity.class));
                finish();
            }
        });
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
