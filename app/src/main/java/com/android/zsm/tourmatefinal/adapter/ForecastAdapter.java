package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.WeatherInfo;
import com.android.zsm.tourmatefinal.model.ForecastDetails;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>{
    private Context context;
    private ArrayList<ForecastDetails> forcastDetails;

    public ForecastAdapter(Context context, ArrayList<ForecastDetails>forcastDetails) {
        this.context = context;
        this.forcastDetails = forcastDetails;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.forecast_single_row,parent,false);
        return new ForecastAdapter.ForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        String image = forcastDetails.get(position).getImage();
        Uri iconUri = Uri.parse("http://openweathermap.org/img/w/" + image + ".png");
        Picasso.with(context).load(iconUri).into(holder.image);

        holder.status.setText(forcastDetails.get(position).getStatus());
        holder.day.setText(forcastDetails.get(position).getDay());
        holder.temp.setText(forcastDetails.get(position).getTemp()+WeatherInfo.tempSign);
        holder.minTemp.setText(forcastDetails.get(position).getMinTemp()+WeatherInfo.tempSign);
        holder.maxTemp.setText(forcastDetails.get(position).getMaxTemp()+WeatherInfo.tempSign);
        holder.sunRise.setText(forcastDetails.get(position).getSunRise()+"%");
        holder.sunSet.setText(forcastDetails.get(position).getSunSet()+" mb");


    }

    @Override
    public int getItemCount() {
        return forcastDetails.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView status;
        TextView day;
        TextView temp;
        TextView maxTemp;
        TextView minTemp;
        TextView sunRise;
        TextView sunSet;
        public ForecastViewHolder(View itemView) {
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
