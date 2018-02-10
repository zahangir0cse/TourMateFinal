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
    private CurrentWeatherResponse currentWeatherResponse;


    public CurrentWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viw = inflater.inflate(R.layout.fragment_current_weather, container, false);

        location = viw.findViewById(R.id.myLocationCurrent);
        temp = viw.findViewById(R.id.tempCurrent);
        image = viw.findViewById(R.id.weatherImageCurrent);
        description = viw.findViewById(R.id.descriptionCurrent);
        date = viw.findViewById(R.id.dateCurrent);
        maxTemp = viw.findViewById(R.id.maxTempCurrent);
        minTemp = viw.findViewById(R.id.minTempCurrent);
        sunRise = viw.findViewById(R.id.sunRiseCurrent);
        sunSet = viw.findViewById(R.id.sunSetCurrent);
        humedity = viw.findViewById(R.id.humedityCurrent);
        pressure = viw.findViewById(R.id.pressureCurrent);
        wind = viw.findViewById(R.id.windCurrent);

        Call<CurrentWeatherResponse> call = new Utility().getCurrentWeatherCallInstance();
        call.enqueue(new Callback<CurrentWeatherResponse>() {

            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if(response.code() == 200){
                    currentWeatherResponse = response.body();
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

                    String minTmp = currentWeatherResponse.getMain().getTempMin().toString();
                    minTemp.setText(minTmp+WeatherInfo.tempSign);

                    String maxTmp = currentWeatherResponse.getMain().getTempMax().toString();
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

                    String hmedity = currentWeatherResponse.getMain().getHumidity().toString();
                    humedity.setText(hmedity+"%");

                    String prssure = currentWeatherResponse.getMain().getHumidity().toString();
                    pressure.setText(prssure+" mb");

                    String wnd = currentWeatherResponse.getWind().getSpeed().toString();
                    wind.setText(wnd+" mphn");
                }

            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {

            }
        });

        return viw;
    }

}
