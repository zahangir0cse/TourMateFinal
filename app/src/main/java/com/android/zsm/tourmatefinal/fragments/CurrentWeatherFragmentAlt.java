package com.android.zsm.tourmatefinal.fragments;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.zsm.tourmatefinal.CurrentWeatherResponse;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.android.zsm.tourmatefinal.WeatherService;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentWeatherFragmentAlt extends Fragment {

    private TextView cityTV, dateTV, tempTV, sunSetTV, sunRiseTV, maxTempTV, minTempTV, pressureTV, humanityTV, detailsTV;
    private ImageView weatherIconIV;
    private WeatherService service;
    private CurrentWeatherResponse currentWeatherResponse;
    public static String units = "metric";
    public static String tempSign = "°C";

    public CurrentWeatherFragmentAlt() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        cityTV = view.findViewById(R.id.city_field);
        weatherIconIV = view.findViewById(R.id.weather_icon);
        dateTV = view.findViewById(R.id.updated_field);
        tempTV = view.findViewById(R.id.current_temperature_field);
        sunRiseTV = view.findViewById(R.id.sunrise);
        sunSetTV = view.findViewById(R.id.sunset);
        maxTempTV = view.findViewById(R.id.maxTemp);
        minTempTV = view.findViewById(R.id.minTemp);
        pressureTV = view.findViewById(R.id.pressure_field);
        humanityTV = view.findViewById(R.id.humidity_field);
        detailsTV = view.findViewById(R.id.details_field);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(WeatherInfo.OWM_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(WeatherService.class);
        String endUrl = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", WeatherInfo.latitude, WeatherInfo.longitude, units,
                "774dabb02c987b69cfd863bd9a80f8a5");
        Call<CurrentWeatherResponse> call = service.getCurrentWeather(endUrl);
        call.enqueue(new Callback<CurrentWeatherResponse>() {

            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if(response.code() == 200){
                    currentWeatherResponse = response.body();
                    String loc = currentWeatherResponse.getName();
                    String country = currentWeatherResponse.getSys().getCountry();
                    cityTV.setText(loc+", "+country);

                    double tmp = currentWeatherResponse.getMain().getTemp();
                    int mm = (int) tmp;
                    String nn = String.valueOf(mm);
                    tempTV.setText(nn+tempSign);

                    String iconString = currentWeatherResponse.getWeather().get(0).getIcon();
                    Uri iconUri = Uri.parse("http://openweathermap.org/img/w/"+iconString+".png");
                    Picasso.with(getActivity().getApplicationContext()).load(iconUri).into(weatherIconIV);

                    String desc = currentWeatherResponse.getWeather().get(0).getDescription().toString();
                    detailsTV.setText(desc);

                    long currentTime = currentWeatherResponse.getDt();
                    Date cTimeDateFormte = new Date(currentTime*1000L);
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy");
                    SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
                    String finalDate = df.format(cTimeDateFormte.getTime());
                    String curTime = dfTime.format(cTimeDateFormte.getTime());
                    dateTV.setText(finalDate+"\n"+curTime);

                    String minTmp = String.valueOf(currentWeatherResponse.getMain().getTempMin());
                    minTempTV.setText(minTmp+tempSign);

                    String maxTmp = String.valueOf(currentWeatherResponse.getMain().getTempMax());
                    maxTempTV.setText(maxTmp+tempSign);

                    long unix_sunrise = currentWeatherResponse.getSys().getSunrise();
                    Date date_sunrise = new Date(unix_sunrise*1000L);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a");
                    String sunRs = df2.format(date_sunrise.getTime());
                    sunRiseTV.setText(sunRs);

                    long unix_sunset = currentWeatherResponse.getSys().getSunset();
                    Date date_sunset = new Date(unix_sunset*1000L);
                    SimpleDateFormat df3 = new SimpleDateFormat("hh:mm a");
                    String sunSt = df3.format(date_sunset.getTime());
                    sunSetTV.setText(sunSt);

                    String hmedity = String.valueOf(currentWeatherResponse.getMain().getHumidity());
                    humanityTV.setText(hmedity+"%");

                    String prssure = String.valueOf(currentWeatherResponse.getMain().getHumidity());
                    pressureTV.setText(prssure+" mb");
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {

            }
        });

        return view;
    }

}
