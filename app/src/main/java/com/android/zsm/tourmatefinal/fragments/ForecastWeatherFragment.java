package com.android.zsm.tourmatefinal.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.zsm.tourmatefinal.model.ForecastDetails;
import com.android.zsm.tourmatefinal.response.ForecastWeatherResponse;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.adapter.ForecastAdapter;
import com.android.zsm.tourmatefinal.utility.Utility;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastWeatherFragment extends Fragment {
    private RecyclerView recyclerView;
    private ForecastWeatherResponse forecastWeatherResponse;
    private ArrayList<ForecastDetails> forecastDetailsArray = new ArrayList<>();
    private ForecastDetails forecastDetails;
    private ForecastAdapter forecastAdapter;
    private Calendar calendar;

    private String iconString, statusString, dayString, tempString, minTString, maxTString, sunRiseString, sunSetString;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewForecast);
        calendar = Calendar.getInstance();

        Call<ForecastWeatherResponse> call = new Utility().getForecastWeatherCallInstance();
        call.enqueue(new Callback<ForecastWeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForecastWeatherResponse> call, @NonNull Response<ForecastWeatherResponse> response) {
                if(response.code() == 200){
                    forecastWeatherResponse = response.body();
                    ArrayList<ForecastDetails> details = new ArrayList<>();
                    for( int i = 0; i < forecastWeatherResponse.getList().size(); i++) {
                        iconString = forecastWeatherResponse.getList().get(i).getWeather().get(0).getIcon();
                        statusString = forecastWeatherResponse.getList().get(i).getWeather().get(0).getDescription();
                        long unix_day = forecastWeatherResponse.getList().get(i).getDt();
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

                        tempString = String.valueOf(forecastWeatherResponse.getList().get(i).getMain().getTemp().intValue());

                        minTString = String.valueOf(forecastWeatherResponse.getList().get(i).getMain().getTempMin().intValue());

                        maxTString = String.valueOf(forecastWeatherResponse.getList().get(i).getMain().getTempMax().intValue());

                        sunRiseString = String.valueOf(forecastWeatherResponse.getList().get(i).getMain().getHumidity().intValue());

                        sunSetString = String.valueOf(forecastWeatherResponse.getList().get(i).getMain().getPressure().intValue());

                        forecastDetails = new ForecastDetails(iconString,statusString,dayString,tempString,minTString,maxTString,sunRiseString,sunSetString);
                        details.add(forecastDetails);
                    }
                    forecastDetailsArray = details;
                    forecastAdapter = new ForecastAdapter(getActivity().getApplicationContext(),forecastDetailsArray);
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
                    recyclerView.setLayoutManager(llm);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(forecastAdapter);

                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastWeatherResponse> call, @NonNull Throwable t) {
            }
        });
        return view;
    }
}
