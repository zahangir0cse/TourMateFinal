package com.android.zsm.tourmatefinal.service;

import com.android.zsm.tourmatefinal.model.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);
}
