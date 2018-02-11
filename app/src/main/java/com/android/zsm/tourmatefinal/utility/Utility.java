package com.android.zsm.tourmatefinal.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import com.android.zsm.tourmatefinal.DirectionMap;
import com.android.zsm.tourmatefinal.EventList;
import com.android.zsm.tourmatefinal.LocationMap;
import com.android.zsm.tourmatefinal.LoginActivity;
import com.android.zsm.tourmatefinal.NearByPlaceActivity;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.UserProfile;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.android.zsm.tourmatefinal.preference.LocationPreference;
import com.android.zsm.tourmatefinal.response.CurrentWeatherResponse;
import com.android.zsm.tourmatefinal.response.ForecastWeatherResponse;
import com.android.zsm.tourmatefinal.service.CurrentWeatherService;
import com.android.zsm.tourmatefinal.service.IGoogleAPIService;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 122;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 121;
    private static final String GOOGLE_API_URL="https://maps.googleapis.com";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&  ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.CAMERA) ) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Camere use and External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void onOptionSelectedUtil(MenuItem item, Context context, Activity activity, FragmentActivity fragmentActivity) {
        switch (item.getItemId()) {
            case R.id.hm:
                activity.startActivity(new Intent(context, EventList.class));
                break;
            case R.id.events:
                activity.startActivity(new Intent(context, EventList.class));
                break;
            case R.id.location_map:
                activity.startActivity(new Intent(context, LocationMap.class));
                break;
            case R.id.nearplace:
                activity.startActivity(new Intent(context, NearByPlaceActivity.class));
                break;
            case R.id.direction:
                activity.startActivity(new Intent(context, DirectionMap.class));
                break;
            case R.id.profile:
                activity.startActivity(new Intent(context, UserProfile.class));
                break;
            case R.id.weather_info:
                activity.startActivity(new Intent(context, WeatherInfo.class));
                break;
            case R.id.logout:
                logoutUser(activity, fragmentActivity, context);
                break;
        }
    }

    private void logoutUser(final Activity activity, FragmentActivity fragmentActivity, final Context context) {
        AuthUI.getInstance().signOut(fragmentActivity).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activity.startActivity(new Intent (context,LoginActivity.class));
                activity.finish();
            }
        });
    }

    public void onPrepareOptionsMenuUtil(Menu menu, FirebaseUser user){
        MenuItem search = menu.findItem(R.id.search);
        MenuItem celsiusItem = menu.findItem(R.id.tempcc);
        MenuItem fahrenheitItem = menu.findItem(R.id.tempff);
        MenuItem eventItem = menu.findItem(R.id.events);
        MenuItem mapItem = menu.findItem(R.id.location_map);
        MenuItem nearPlaceItem = menu.findItem(R.id.nearplace);
        MenuItem mapDirectionItem = menu.findItem(R.id.direction);
        MenuItem weatherItem = menu.findItem(R.id.weather_info);
        MenuItem logoutItem = menu.findItem(R.id.logout);
        MenuItem myProfile = menu.findItem(R.id.profile);
        search.setVisible(true);
        celsiusItem.setVisible(false);
        fahrenheitItem.setVisible(false);
        eventItem.setVisible(true);
        mapItem.setVisible(true);
        nearPlaceItem.setVisible(true);
        mapDirectionItem.setVisible(false);
        weatherItem.setVisible(true);
        if (user != null) {
            logoutItem.setVisible(true);
            myProfile.setVisible(true);
        }
    }

    public void onCreateOptionsMenuUtil(Menu menu,Activity activity, AppCompatActivity appCompatActivity){
        appCompatActivity.getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
    }

    public static double getLatitute(Context context){
        LocationPreference locationPreference = getLocationPreferenceInstance(context);
        String latitute = locationPreference.getLastSaveLatitute();
        if (latitute != null){
            return Double.parseDouble(latitute);
        }else {
            return 23.777176;
        }
    }

    public static double getLongitute(Context context){
        LocationPreference locationPreference = getLocationPreferenceInstance(context);
        String longitute = locationPreference.getLastSaveLatitute();
        if (longitute != null){
            return Double.parseDouble(longitute);
        }else {
            return 90.399452;
        }
    }

    private static LocationPreference getLocationPreferenceInstance(Context context){
        LocationPreference locationPreference = new LocationPreference(context);
        return locationPreference;
    }

    public Retrofit getRetrofitInstance(String baseUrl){
        return new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public Call<CurrentWeatherResponse> getCurrentWeatherCallInstance(){
        Retrofit retrofit = getRetrofitInstance(WeatherInfo.OWM_BASE_URL);
        CurrentWeatherService service = retrofit.create(CurrentWeatherService.class);
        String url = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", WeatherInfo.latitude, WeatherInfo.longitude, WeatherInfo.units,
                    "774dabb02c987b69cfd863bd9a80f8a5");
        Call<CurrentWeatherResponse> call = service.getCurrentWeather(url);
        return call;
    }

    public Call<ForecastWeatherResponse> getForecastWeatherCallInstance(){
        Retrofit retrofit = getRetrofitInstance(WeatherInfo.OWM_BASE_URL);
        CurrentWeatherService service = retrofit.create(CurrentWeatherService.class);
        String url = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s", WeatherInfo.latitude, WeatherInfo.longitude, WeatherInfo.units,
                "774dabb02c987b69cfd863bd9a80f8a5");
        Call<ForecastWeatherResponse> call = service.getForecastWeather(url);
        return call;
    }

    public IGoogleAPIService getGoogleAPIService(){
        return getRetrofitInstance(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

}