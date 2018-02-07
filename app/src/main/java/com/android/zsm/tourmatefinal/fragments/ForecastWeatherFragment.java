package com.android.zsm.tourmatefinal.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.ForecastWeatherResponse;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherService;
import com.android.zsm.tourmatefinal.adapter.WeatherAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ForecastWeatherFragment extends Fragment {
    public RecyclerView mRecyclerView;
    public WeatherAdapter weatherAdapter;
    public Context context;
    private String funit;
    private int wcount;
    private double lat;
    private double lon;
    private String mess;
    TextView cityField;

    public ForecastWeatherFragment() {
    }

    public static ForecastWeatherFragment newInstance(Bundle fb) {
        ForecastWeatherFragment ffragment = new ForecastWeatherFragment();
        Bundle args = new Bundle();
        args = fb;
        ffragment.setArguments(args);
        return ffragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            funit = getArguments().getString("unit");
            wcount = getArguments().getInt("foreCastCount");
            lon = getArguments().getDouble("lon");
            lat = getArguments().getDouble("lat");
            mess = getArguments().getString("mess");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        cityField = fragmentView.findViewById(R.id.showmessage);
        mRecyclerView = fragmentView.findViewById(R.id.mRecyclerView);
        boolean connected = checkInternetConnection();
        if (connected) {
            if (mess == "Invalid city name") {
                cityField.setText(mess);
                cityField.setVisibility(View.VISIBLE);
            } else {
                cityField.setVisibility(View.INVISIBLE);
                getForeCastWeather();
            }
        } else {
            cityField.setText("Please check your internet connection");
            cityField.setVisibility(View.VISIBLE);
        }
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
// add your code here which executes after the execution of onCreateView() method.


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
// add your code here which executes when the host activity is created.
    }

    @Override
    public void onStart() {
        super.onStart();
// add your code here which executes when the Fragment gets visible.
        //  mytext.setText(String.valueOf(fwlist.size()));
    }

    @Override
    public void onResume() {
        super.onResume();
        //  mytext.setText(String.valueOf(fwlist.size()));
// add your code here which executes when the Fragment is visible and intractable.
    }

    public void getForeCastWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.weather_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        String urlString = String.format("forecast?lat=%f&lon=%f&units=%s&type=accurate&appid=%s", lat, lon, funit, getResources().getString(R.string.weather_api));
        Call<ForecastWeatherResponse> call = service.getForecastWeather(urlString);
        call.enqueue(new Callback<ForecastWeatherResponse>() {
            @Override
            public void onResponse(Call<ForecastWeatherResponse> call, Response<ForecastWeatherResponse> fresponse) {
                if (fresponse.code() == 200) {
                    ForecastWeatherResponse forecastWeatherResponse =
                            fresponse.body();
                    ArrayList<ForecastWeatherResponse.List> forecastList = forecastWeatherResponse.getList();
                    if (forecastList.size() > 0) {
                        weatherAdapter = new WeatherAdapter(getContext(), forecastList, funit);
                        LinearLayoutManager llm = new LinearLayoutManager(getContext());
                        //GridLayoutManager glm = new GridLayoutManager(context,1);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        mRecyclerView.setLayoutManager(llm);
                        mRecyclerView.setAdapter(weatherAdapter);
                    } else {
                        cityField.setText("No data found please try again");
                        cityField.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ForecastWeatherResponse> call, Throwable t) {
                boolean connected = checkInternetConnection();
                if (connected) {
                    cityField.setText("There is a problem to show weatherinfo");
                    cityField.setVisibility(View.VISIBLE);
                } else {
                    cityField.setText("Please check your internet connection");
                    cityField.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }
}
