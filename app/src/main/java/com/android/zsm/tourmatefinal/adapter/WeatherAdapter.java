package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.zsm.tourmatefinal.response.ForecastWeatherResponse;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.android.zsm.tourmatefinal.model.ForecastDetails;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private Context context;
    private ArrayList<ForecastDetails> forecastList;
    private ForecastWeatherResponse forecastWeatherResponse;

    public WeatherAdapter(Context context, ArrayList<ForecastDetails> forecastList) {
        this.context = context;
        this.forecastList = forecastList;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.forecast_single_row, parent, false);
        return new WeatherViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {

        String imageName = forecastList.get(position).getImage() + ".png";
        Picasso.with(context).load(WeatherInfo.IMAGE_PATH + imageName).into(holder.image);

        holder.day.setText(forecastList.get(position).getDay());
        holder.temp.setText(forecastList.get(position).getTemp());
        holder.status.setText(forecastList.get(position).getDay());
        holder.sunRise.setText(forecastList.get(position).getSunRise());
        holder.sunSet.setText(forecastList.get(position).getSunSet());
        holder.minTemp.setText(String.valueOf(forecastList.get(position).getMinTemp()) + WeatherInfo.tempSign);
        holder.maxTemp.setText(String.valueOf(forecastList.get(position).getMaxTemp()) +  WeatherInfo.tempSign);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView status;
        TextView day;
        TextView temp;
        TextView maxTemp;
        TextView minTemp;
        TextView sunRise;
        TextView sunSet;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView1);
            status = itemView.findViewById(R.id.status1);
            day = itemView.findViewById(R.id.day1);
            temp = itemView.findViewById(R.id.temp1);
            maxTemp = itemView.findViewById(R.id.maxTemp1);
            minTemp = itemView.findViewById(R.id.minTemp1);
            sunRise = itemView.findViewById(R.id.sunRise1);
            sunSet = itemView.findViewById(R.id.sunSet1);
        }

    }
}
