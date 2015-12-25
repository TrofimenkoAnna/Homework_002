package com.atrofimenko.weather.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atrofimenko.weather.R;
import com.atrofimenko.weather.Util.RealmOneForecast;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentDetails extends Fragment {

    private int position;

    public FragmentDetails() {
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


        Realm realm = Realm.getInstance(getContext());
        RealmResults<RealmOneForecast> results = realm.where(RealmOneForecast.class).findAll();
        RealmOneForecast theForecast = results.get(position);

        String dateString = theForecast.getDtTxt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {date = dateFormat.parse(dateString);} catch (ParseException e) {e.printStackTrace();}
        Calendar cal = Calendar.getInstance();
        if (date != null) {cal.setTime(date);}

        String week = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String hour = new SimpleDateFormat("HH:mm").format(cal.getTime());
        weekTextView.setText(week);
        dateTextView.setText(String.format(getResources().getString(R.string.dd_mmm_yyyy), day, month, year));
        hourTextView.setText(hour);

        mainTextView.setText(theForecast.getMain());
        temperatureMaxTextView.setText(String.valueOf(theForecast.getTempMax()));
        temperatureMinTextView.setText(String.valueOf(theForecast.getTempMin()));
        humidityTextView.setText(String.valueOf(theForecast.getHumidity()));
        windSpeedTextView.setText(String.valueOf(theForecast.getWind()));
        windDirectionTextView.setText(String.valueOf(theForecast.getDeg()));

        Picasso.with(getContext())
                .load(String.format(getResources().getString(R.string.icon_url), theForecast.getIcon()))
                .into(iconImageView);
    }

    public void setItemContent(int position) {
        this.position = position;
    }
}
