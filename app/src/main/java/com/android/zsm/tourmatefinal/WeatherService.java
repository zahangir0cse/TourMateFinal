package com.android.zsm.tourmatefinal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherService {
    @GET()
    Call<CurrentWeatherResponse>getCurrentWeather(@Url String urlString);
    @GET()
    Call<ForecastWeatherResponse>getForecastWeather(@Url String urlString);
}
