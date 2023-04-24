package com.example.streamingapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRvAdapter extends RecyclerView.Adapter<WeatherRvAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRvmodel> weatherRvmodelArrayList;

    public WeatherRvAdapter(Context context, ArrayList<WeatherRvmodel> weatherRvmodels) {
        this.context = context;
        this.weatherRvmodelArrayList = weatherRvmodels;
    }

    @NonNull
    @Override
    public WeatherRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherRvAdapter.ViewHolder holder, int position) {

        WeatherRvmodel rvmodel = weatherRvmodelArrayList.get(position);
        holder.temptv.setText(rvmodel.getTemperature()+"°c");
        Log.e("TAG_7", "onBindViewHolder: "+rvmodel.getTemperature());
        holder.windtv.setText(rvmodel.getWindspeed() + "Km/h");
        Log.e("TAG_8", "onBindViewHolder: "+rvmodel.getWindspeed());
        Picasso.get().load("http:".concat(rvmodel.getIcon())).into(holder.condition);
        Log.e("TAG_9", "onBindViewHolder: "+rvmodel.getIcon());
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outp=new SimpleDateFormat("hh:mm aa");
        try {
            Date date=dateFormat.parse(rvmodel.getTime());
            holder.time.setText(outp.format(date));
            Log.e("TAG_10", "onBindViewHolder: "+rvmodel.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRvmodelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView windtv, temptv, time;
        ImageView condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windtv = itemView.findViewById(R.id.tvwindspeed);
            temptv = itemView.findViewById(R.id.idTvtemperature);
            time = itemView.findViewById(R.id.idTvtime);
            condition = itemView.findViewById(R.id.idIVcondition);
        }
    }
}
