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
import com.android.zsm.tourmatefinal.response.CurrentWeatherResponse;
import com.android.zsm.tourmatefinal.service.CurrentWeatherService;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentWeatherFragment extends Fragment {

    private TextView location, temp, description, date, maxTemp, minTemp, sunSet, sunRise, humedity, pressure, wind;
    private ImageView image;
    private CurrentWeatherService service;
    private CurrentWeatherResponse currentWeatherResponse;

    public CurrentWeatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        location = view.findViewById(R.id.myLocation);
        temp = view.findViewById(R.id.temp);
        image = view.findViewById(R.id.weatherImage);
        description = view.findViewById(R.id.description1);
        date = view.findViewById(R.id.date);
        maxTemp = view.findViewById(R.id.maxTemp);
        minTemp = view.findViewById(R.id.minTemp);
        sunRise = view.findViewById(R.id.sunRise);
        sunSet = view.findViewById(R.id.sunSet);
        humedity = view.findViewById(R.id.humedity);
        pressure = view.findViewById(R.id.pressure);
        wind = view.findViewById(R.id.wind);

        Call<CurrentWeatherResponse> call = new Utility().getCurrentWeatherCallInstance();
        call.enqueue(new Callback<CurrentWeatherResponse>() {

            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.code() == 200) {
                    String loc = currentWeatherResponse.getName();
                    String country = currentWeatherResponse.getSys().getCountry();
                    location.setText(loc+", "+country);

                    double tmp = currentWeatherResponse.getMain().getTemp();
                    int mm = (int) tmp;
                    String nn = String.valueOf(mm);
                    temp.setText(nn+WeatherInfo.tempSign);

                    String iconString = currentWeatherResponse.getWeather().get(0).getIcon();
                    Uri iconUri = Uri.parse("http://openweathermap.org/img/w/"+iconString+".png");
                    Picasso.with(getActivity().getApplicationContext()).load(iconUri).into(image);

                    String desc = currentWeatherResponse.getWeather().get(0).getDescription().toString();
                    description.setText(desc);

                    long currentTime = currentWeatherResponse.getDt();
                    Date cTimeDateFormte = new Date(currentTime*1000L);
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy");
                    SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
                    String finalDate = df.format(cTimeDateFormte.getTime());
                    String curTime = dfTime.format(cTimeDateFormte.getTime());
                    date.setText(finalDate+"\n"+curTime);

                    String minTmp = String.valueOf(currentWeatherResponse.getMain().getTempMin());
                    minTemp.setText(minTmp+WeatherInfo.tempSign);

                    String maxTmp = String.valueOf(currentWeatherResponse.getMain().getTempMax());
                    maxTemp.setText(maxTmp+WeatherInfo.tempSign);

                    long unix_sunrise = currentWeatherResponse.getSys().getSunrise();
                    Date date_sunrise = new Date(unix_sunrise*1000L);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a");
                    String sunRs = df2.format(date_sunrise.getTime());
                    sunRise.setText(sunRs);

                    long unix_sunset = currentWeatherResponse.getSys().getSunset();
                    Date date_sunset = new Date(unix_sunset*1000L);
                    SimpleDateFormat df3 = new SimpleDateFormat("hh:mm a");
                    String sunSt = df3.format(date_sunset.getTime());
                    sunSet.setText(sunSt);

                    String humanidity = String.valueOf(currentWeatherResponse.getMain().getHumidity());
                    humedity.setText(humanidity+"%");

                    String prssure = String.valueOf(currentWeatherResponse.getMain().getHumidity());
                    pressure.setText(prssure+" mb");

                    String wnd = String.valueOf(currentWeatherResponse.getWind().getSpeed());
                    wind.setText(wnd+" mphn");
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {

            }
        });

        return view;
    }

}
