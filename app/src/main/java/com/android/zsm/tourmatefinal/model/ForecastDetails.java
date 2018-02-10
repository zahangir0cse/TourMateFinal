package com.android.zsm.tourmatefinal.model;

/**
 * Created by Zahangir Alam on 2018-02-10.
 */

public class ForecastDetails {
    private String image;
    private String status;
    private String day;
    private String temp;
    private String maxTemp;
    private String minTemp;
    private String sunRise;
    private String sunSet;

    public ForecastDetails(String image, String status, String day, String temp, String maxTemp, String minTemp, String sunRise, String sunSet) {
        this.image = image;
        this.status = status;
        this.day = day;
        this.temp = temp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.sunRise = sunRise;
        this.sunSet = sunSet;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }
}
