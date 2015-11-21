package com.atrofimenko.weather_hw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by Anna_T on 21.11.2015.
 */
public class Details_Fragment extends Fragment {
        private int position;

        public Details_Fragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_details, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            TextView weekTextView = (TextView) view.findViewById(R.id.week_detail);
            TextView dateTextView = (TextView) view.findViewById(R.id.day_month_year_detail);
            TextView hourTextView = (TextView) view.findViewById(R.id.hour_detail);
            ImageView iconImageView = (ImageView) view.findViewById(R.id.icon_detail);
            TextView mainTextView = (TextView) view.findViewById(R.id.main_detail);
            TextView temperatureMaxTextView = (TextView) view.findViewById(R.id.temperature_max_detail);
            TextView temperatureMinTextView = (TextView) view.findViewById(R.id.temperature_min_detail);
            TextView humidityTextView = (TextView) view.findViewById(R.id.humiditi_detail);
            TextView windSpeedTextView = (TextView) view.findViewById(R.id.wind_detail);
            TextView windDirectionTextView = (TextView) view.findViewById(R.id.wind_direction_detail);

            HashMap<String, String> aForecast = List_Fragment.forecast.get(position);
            weekTextView.setText(aForecast.get("week"));
            dateTextView.setText(String.format(getResources()
                    .getString(R.string.dd_mmm_yyyy), aForecast.get("day"), aForecast.get("month"), aForecast.get("year")));
            hourTextView.setText(aForecast.get("hour"));
            mainTextView.setText(aForecast.get("main"));
            temperatureMaxTextView.setText(aForecast.get("temperatureMax"));
            temperatureMinTextView.setText(aForecast.get("temperatureMin"));
            humidityTextView.setText(aForecast.get("humidity"));
            windSpeedTextView.setText(aForecast.get("wind"));
            windDirectionTextView.setText(aForecast.get("direction"));

            Picasso.with(getContext())
                    .load(String.format(getResources().getString(R.string.icon_url), aForecast.get("icon")))
                    .into(iconImageView);
        }

        public void setItemContent(int position) {
            this.position = position;
        }
}
