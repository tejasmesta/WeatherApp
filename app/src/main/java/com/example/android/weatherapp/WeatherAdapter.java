package com.example.android.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherModalClass> weatherModalClassArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModalClass> weatherModalClassArrayList) {
        this.context = context;
        this.weatherModalClassArrayList = weatherModalClassArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherModalClass weatherModalClass = weatherModalClassArrayList.get(position);

        holder.temp.setText(weatherModalClass.getTemp()+"°C");
        holder.windSpeed.setText(weatherModalClass.getWindSpeed()+"Km/h");
        Picasso.get().load("http:".concat(weatherModalClass.getIcon())).into(holder.icon);

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try
        {
            Date t = input.parse(weatherModalClass.getTime());
            holder.time.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherModalClassArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView time,temp,windSpeed;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            temp = itemView.findViewById(R.id.tempcard);
            windSpeed = itemView.findViewById(R.id.windSPEED);
            icon = itemView.findViewById(R.id.WeatherConditionIcon);
        }
    }
}
