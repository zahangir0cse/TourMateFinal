package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.ForecastWeatherResponse;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private Context context;
    private ArrayList<ForecastWeatherResponse.List> forecastList;
    private String unit;
    private ForecastWeatherResponse forecastWeatherResponse;

    public WeatherAdapter(Context context, ArrayList<ForecastWeatherResponse.List> forecastList, String un) {
        this.context = context;
        this.forecastList = forecastList;
        this.unit = un;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.forecast_single_row, parent, false);
        return new WeatherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        DateFormat df = DateFormat.getDateTimeInstance();
        String updatedOn = df.format(new Date(forecastList.get(position).getDt() * 1000));
        String imagename = forecastList.get(position).getWeather().get(0).getIcon() + ".png";
        Picasso.with(context).load(WeatherInfo.IMAGE_PATH + imagename).into(holder.weatherIcon);
        String unitvalue = holder.getdegree(unit);
        holder.tDateTV.setText(updatedOn);
        holder.sunRiseTV.setText("Hum: " + String.valueOf(forecastList.get(position).getMain().getHumidity()));
        holder.sunSetTV.setText("Pre: " + String.valueOf(forecastList.get(position).getMain().getPressure()));
        holder.minTempTV.setText("Min Temp: " + String.valueOf(forecastList.get(position).getMain().getTempMin()) + unitvalue);
        holder.maxTempTV.setText("Max Temp: " + String.valueOf(forecastList.get(position).getMain().getTempMax()) + unitvalue);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView weatherIcon;
        TextView tDateTV;
        TextView sunRiseTV, sunSetTV, minTempTV, maxTempTV;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            weatherIcon = itemView.findViewById(R.id.weathericon);
            tDateTV = itemView.findViewById(R.id.tDate);
            sunRiseTV = itemView.findViewById(R.id.sunRise);
            sunSetTV = itemView.findViewById(R.id.sunset);
            minTempTV = itemView.findViewById(R.id.mintemp);
            maxTempTV = itemView.findViewById(R.id.maxTemp);

        }

        public String getdegree(String unt) {
            String showdegree = "";
            switch (unt) {
                case "metric":
                    showdegree = (char) 0x00B0 + " C";
                    break;
                case "imperial":
                    showdegree = (char) 0x00B0 + " F";
                    break;
            }
            return showdegree;
        }

        public String convertLongToTime(long milliseconds) /* This is your topStory.getTime()*1000 */ {
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            TimeZone tz = TimeZone.getDefault();
            sdf.setTimeZone(tz);
            return sdf.format(calendar.getTime());


        }
    }


}
