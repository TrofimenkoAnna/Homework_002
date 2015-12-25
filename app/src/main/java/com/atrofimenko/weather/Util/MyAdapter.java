package com.atrofimenko.weather.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atrofimenko.weather.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class MyAdapter extends RealmBaseAdapter<RealmOneForecast> {

    public MyAdapter(Context context, RealmResults<RealmOneForecast> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.list_item, parent, false);  // inflate the layout
            // Set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.day = (TextView) convertView.findViewById(R.id.day);
            viewHolder.month = (TextView) convertView.findViewById(R.id.month);
            viewHolder.year = (TextView) convertView.findViewById(R.id.year);
            viewHolder.week = (TextView) convertView.findViewById(R.id.week);
            viewHolder.hour = (TextView) convertView.findViewById(R.id.hour);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.main = (TextView) convertView.findViewById(R.id.main);
            viewHolder.temp = (TextView) convertView.findViewById(R.id.temperature);
            // Store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RealmOneForecast theForecast = realmResults.get(position);  // forecast item based on the position
        // Assign values if the forecast is not null
        if (theForecast != null) {

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
            String hour = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(cal.getTime());

            viewHolder.day.setText(day);
            viewHolder.month.setText(month);
            viewHolder.year.setText(year);
            viewHolder.hour.setText(hour);
            viewHolder.week.setText(week);
            viewHolder.temp.setText(temperatureFormat(theForecast.getTemp()));
            viewHolder.main.setText(theForecast.getMain());
            Picasso.with(context)
                    .load(String.format(context.getResources().getString(R.string.icon_url), theForecast.getIcon()))
                    .into(viewHolder.icon);
        }
        return convertView;
    }

    // ViewHolder
    static class ViewHolder {
        TextView day;
        TextView month;
        TextView year;
        TextView week;
        TextView hour;
        ImageView icon;
        TextView main;
        TextView temp;
    }

    private String temperatureFormat(double t) {
        String f = "";
        long tRound = Math.round(t);
        if (tRound > 0) { f = "+"; }
        return f + String.valueOf(tRound);
    }

}