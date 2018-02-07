package com.android.zsm.tourmatefinal.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.zsm.tourmatefinal.fragments.CurrentWeatherFragmentAlt;
import com.android.zsm.tourmatefinal.fragments.ForecastWeatherFragment;


public class WeatherTabAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private Bundle tb;

    public WeatherTabAdapter(FragmentManager fm, int NumOfTabs, Bundle b) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.tb = b;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CurrentWeatherFragmentAlt();
            case 1:
                return ForecastWeatherFragment.newInstance(tb);
            default:
                return new CurrentWeatherFragmentAlt();
        }

    }












/*

    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CurrentWeatherFragment tab1 = new CurrentWeatherFragment();
                return tab1;
            case 1:
                ForecastWeatherFragment tab2 = new ForecastWeatherFragment();
                return tab2;
           default:
                return null;
        }
    }*/

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}