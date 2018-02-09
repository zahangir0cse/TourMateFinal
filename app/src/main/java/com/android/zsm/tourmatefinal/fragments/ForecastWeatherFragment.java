package com.android.zsm.tourmatefinal.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.ForecastWeatherResponse;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.android.zsm.tourmatefinal.WeatherService;
import com.android.zsm.tourmatefinal.adapter.WeatherAdapter;
import com.android.zsm.tourmatefinal.model.ForecastDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ForecastWeatherFragment extends Fragment {
    private RecyclerView recyclerView;
    private WeatherService service;
    private ForecastWeatherResponse forcastWeatherResponse;
    private ArrayList<ForecastDetails> forcastDetailsArray = new ArrayList<>();
    private ForecastDetails forcastDetails;
    private WeatherAdapter forcastAdapter;
    private Calendar calendar;
    public static String units = "metric";
    public static String tempSign = "°C";

    private String iconString, statusString, dayString, tempString, minTString, maxTString, sunRiseString, sunSetString;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        recyclerView = view.findViewById(R.id.mRecyclerView);

        calendar = Calendar.getInstance();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherInfo.OWM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WeatherService.class);
        String endUrl = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s", WeatherInfo.latitude, WeatherInfo.longitude, units,
                "774dabb02c987b69cfd863bd9a80f8a5");
        Call<ForecastWeatherResponse> call = service.getForecastWeather(endUrl);
        call.enqueue(new Callback<ForecastWeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForecastWeatherResponse> call, @NonNull Response<ForecastWeatherResponse> response) {
                if(response.code() == 200){
                    forcastWeatherResponse = response.body();
                    ArrayList<ForecastDetails> details = new ArrayList<>();
                    for( int i = 0; i < forcastWeatherResponse.getList().size(); i++) {
                        iconString = forcastWeatherResponse.getList().get(i).getWeather().get(0).getIcon();
                        statusString = forcastWeatherResponse.getList().get(i).getWeather().get(0).getDescription();
                        long unix_day = forcastWeatherResponse.getList().get(i).getDt();
                        Date date = new Date(unix_day*1000L);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMd, hha");
                        SimpleDateFormat df2 = new SimpleDateFormat("hha");
                        String todayTime = df2.format(date.getTime());
                        SimpleDateFormat dfMy = new SimpleDateFormat("d");
                        int weatherDate = Integer.parseInt(dfMy.format(date.getTime()));
                        int sysDate = Integer.parseInt(dfMy.format(calendar.getTime()));
                        if( weatherDate == sysDate){
                            dayString = "Today, "+todayTime;
                        }
                        else if( (weatherDate-1) == sysDate){
                            dayString = "Tomorrow, "+todayTime;
                        }
                        else {
                            dayString = df.format(date.getTime());
                        }

                        tempString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getTemp());

                        minTString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getTempMin());

                        maxTString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getTempMax());

                        sunRiseString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getHumidity());

                        sunSetString = String.valueOf(forcastWeatherResponse.getList().get(i).getMain().getPressure());

                        forcastDetails = new ForecastDetails(iconString,statusString,dayString,tempString,minTString,maxTString,sunRiseString,sunSetString);
                        details.add(forcastDetails);
                    }
                    forcastDetailsArray = details;
                    forcastAdapter = new WeatherAdapter(getActivity().getApplicationContext(),forcastDetailsArray);
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(llm);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(forcastAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastWeatherResponse> call, @NonNull Throwable t) {
            }
        });
        return view;
    }
}
