package com.android.zsm.tourmatefinal.service;

import com.android.zsm.tourmatefinal.response.CurrentWeatherResponse;
import com.android.zsm.tourmatefinal.response.ForecastWeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CurrentWeatherService {
    @GET()
    Call<CurrentWeatherResponse>getCurrentWeather(@Url String urlString);
    @GET()
    Call<ForecastWeatherResponse>getForecastWeather(@Url String urlString);
}
