package com.android.zsm.tourmatefinal.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.zsm.tourmatefinal.R;

public class CurrentWeatherFragmentAlt extends Fragment {


    public CurrentWeatherFragmentAlt() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_current_weather, container, false);
    }

}
