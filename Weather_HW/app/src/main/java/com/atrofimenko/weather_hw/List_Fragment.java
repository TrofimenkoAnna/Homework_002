package com.atrofimenko.weather_hw;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class List_Fragment extends Fragment {
    static ArrayList<HashMap<String, String>> forecast;
    private ListView listView;
    private CallBackInterface myInterface;

    public List_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            myInterface = (CallBackInterface) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " Must implement CallBackInterface");
        }
    }

    public class GetData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        private ArrayList<HashMap<String, String>> getWeatherData(String jsonStr) throws JSONException {
            JSONObject forecast_Json = new JSONObject(jsonStr);
            JSONArray weatherArray = forecast_Json.getJSONArray("list");
            ArrayList<HashMap<String, String>> forecasts = new ArrayList<>();

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject jsonForecast1 = weatherArray.getJSONObject(i);

                Date date = new Date((long) jsonForecast1.getInt("dt") * 1000);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                String week = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
                String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
                String year = String.valueOf(cal.get(Calendar.YEAR));
                String hour = new SimpleDateFormat("HH:mm").format(cal.getTime());

                String temperature = jsonForecast1.getJSONObject("main").get("temp").toString();
                String temperatureMax = jsonForecast1.getJSONObject("main").get("temp_max").toString();
                String temperatureMin = jsonForecast1.getJSONObject("main").get("temp_min").toString();

                String main = jsonForecast1.getJSONArray("weather").getJSONObject(0).getString("main");
                String icon = jsonForecast1.getJSONArray("weather").getJSONObject(0).getString("icon");

                String humidity = jsonForecast1.getJSONObject("main").get("humidity").toString();
                String wind = jsonForecast1.getJSONObject("wind").get("speed").toString();
                String direction = jsonForecast1.getJSONObject("wind").get("deg").toString();

                HashMap<String, String> forecast1 = new HashMap<>();
                forecast1.put("week", week);
                forecast1.put("day", day);
                forecast1.put("month", month);
                forecast1.put("year", year);
                forecast1.put("hour", hour);
                forecast1.put("temperature", temperature);
                forecast1.put("temperatureMax", temperatureMax);
                forecast1.put("temperatureMin", temperatureMin);
                forecast1.put("main", main);
                forecast1.put("icon", icon);
                forecast1.put("humidity", humidity);
                forecast1.put("wind", wind);
                forecast1.put("direction", direction);

                forecasts.add(i, forecast1);
            }
            return forecasts;
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... args) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String forecastStr = null;
            try {
                URL url = new URL(getResources().getString(R.string.openweathermap));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                StringBuilder buffer = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    buffer.append(line);
                if (buffer.length() == 0) {
                    return null;
                }
                forecastStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getWeatherData(forecastStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            if (result != null) {
                forecast = result;
                String[] from = {"day", "month", "year", "week", "hour", "temperature", "main"};
                int[] to = {R.id.day, R.id.month, R.id.year, R.id.week, R.id.hour, R.id.temperature, R.id.main};
                MySimpleAdapter adapter = new MySimpleAdapter(getContext(), result, R.layout.list_item, from, to);
                listView.setAdapter(adapter);
            }
            super.onPostExecute(result);
        }

        public class MySimpleAdapter extends SimpleAdapter {

            public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
                super(context, data, resource, from, to);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                ImageView iconImageView = (ImageView) row.findViewById(R.id.icon);
                Picasso.with(getContext())
                        .load(String.format(getResources().getString(R.string.icon_url), forecast.get(position).get("icon")))
                        .into(iconImageView);
                return row;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetData weatherTask = new GetData();
        weatherTask.execute();
        listView = (ListView) view.findViewById(R.id.listview);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myInterface.updateContent(position);
            }
        });
    }
}
