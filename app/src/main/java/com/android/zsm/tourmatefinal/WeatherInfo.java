package com.android.zsm.tourmatefinal;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.zsm.tourmatefinal.fragments.CurrentWeatherFragment;
import com.android.zsm.tourmatefinal.fragments.ForecastWeatherFragment;
import com.android.zsm.tourmatefinal.response.CurrentWeatherResponse;
import com.android.zsm.tourmatefinal.service.CurrentWeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class WeatherInfo extends AppCompatActivity {
    public static final String OWM_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String IMAGE_PATH = "http://openweathermap.org/img/w/";
    public static double latitude, longitude;
    public static String units = "metric";
    public static String tempSign = "°C";
    private CurrentWeatherResponse currentWeatherResponse;
    private CurrentWeatherService service;
    private FusedLocationProviderClient client;
    private LocationCallback callback;
    private LocationRequest request;
    private Geocoder giocoder;
    private List<Address> addresses;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabPagerAdapter tabPagerAdapter;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        calendar = Calendar.getInstance();
        giocoder = new Geocoder(this);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction()) && !Intent.ACTION_SEARCH.equals(null)){
            String query = intent.getStringExtra(SearchManager.QUERY);

            try {
                List<Address> myLoc = giocoder.getFromLocationName(query, 1);

                latitude = myLoc.get(0).getLatitude();
                longitude = myLoc.get(0).getLongitude();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,CityNameSuggestions.AUTHORITY, CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(query,null);
        }else {
            //LocationPart
            client = LocationServices.getFusedLocationProviderClient(this);
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        try {
                            addresses = giocoder.getFromLocation(latitude,longitude,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            createLocationRequest();
        }
        //code for tabLayout
        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);

        mTabLayout.addTab(mTabLayout.newTab().setText("current weather").setIcon(R.drawable.weather_icon));
        mTabLayout.addTab(mTabLayout.newTab().setText("forecast weather").setIcon(R.drawable.weather_icon));

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(tabPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //MenuItems
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenue_weather,menu);
        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.tempc).setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.tempc:
                units = "metric";
                tempSign = "°C";
                finish();
                startActivity(getIntent());
                break;

            case R.id.tempf:
                units = "imperial";
                tempSign = "°F";
                finish();
                startActivity(getIntent());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {
        private int tabCount;

        public TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new CurrentWeatherFragment();
                case 1:
                    return new ForecastWeatherFragment();
                default:
                    return new CurrentWeatherFragment();
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }

    }

    private void createLocationRequest() {
        request = new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(2500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        client.requestLocationUpdates(request, callback, null);
    }
}
