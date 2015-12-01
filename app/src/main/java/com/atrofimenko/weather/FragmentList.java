package com.atrofimenko.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentList extends Fragment {

    private CallBackInterface myInterface;
    private Realm realm;
    private ListView listView;
    private MyAdapter adapter;

    public FragmentList() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            myInterface = (CallBackInterface) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " Must implement CallbackInterface");
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

        realm = Realm.getInstance(getContext());
        RealmResults<RealmOneForecast> results = realm.where(RealmOneForecast.class).findAll();


        if (!MainActivity.isConnected) {
            if (results.size() == 0) {
                Snackbar.make(view, R.string.no_internet_no_db, Snackbar.LENGTH_INDEFINITE).show();
            } else {
                Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_INDEFINITE).show();
            }
        } else {

            DataLoader weatherTask = new DataLoader();
            weatherTask.execute();
        }

        listView = (ListView) view.findViewById(R.id.listview);
        adapter = new MyAdapter(getContext(), results, true);  // automatic update - true
        listView.setAdapter(adapter);
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

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }


    public class DataLoader extends AsyncTask<Void, Void, Void> {

        // JSON -> Realm
        private void jsonToRealm() throws JSONException {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr;
            Realm realm = Realm.getInstance(getContext());
            try {

                URL url = new URL(getResources().getString(R.string.openweathermap_url));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                forecastJsonStr = buffer.toString(); // JSON as String
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray("list");

                realm.beginTransaction();
                for (int i = 0; i < weatherArray.length(); i++) {
                    JSONObject oneJSONForecast = weatherArray.getJSONObject(i);
                    // Get info from JSO
                    String dtTxt = oneJSONForecast.getString("dt_txt");
                    double temp = Double.parseDouble(oneJSONForecast.getJSONObject("main").get("temp").toString());
                    double tempMax = Double.parseDouble(oneJSONForecast.getJSONObject("main").get("temp_max").toString());
                    double tempMin = Double.parseDouble(oneJSONForecast.getJSONObject("main").get("temp_min").toString());
                    String main = oneJSONForecast.getJSONArray("weather").getJSONObject(0).getString("main");
                    String icon = oneJSONForecast.getJSONArray("weather").getJSONObject(0).getString("icon");
                    double humidity = Double.parseDouble(oneJSONForecast.getJSONObject("main").get("humidity").toString());
                    double wind = Double.parseDouble(oneJSONForecast.getJSONObject("wind").get("speed").toString());
                    double deg = Double.parseDouble(oneJSONForecast.getJSONObject("wind").get("deg").toString());
                    double pressure = Double.parseDouble(oneJSONForecast.getJSONObject("main").get("pressure").toString());
                    // Create new Realm obj and put info into this object
                    RealmOneForecast realmOneForecast = new RealmOneForecast();
                    realmOneForecast.setDtTxt(dtTxt);
                    realmOneForecast.setTemp(temp);
                    realmOneForecast.setTempMax(tempMax);
                    realmOneForecast.setTempMin(tempMin);
                    realmOneForecast.setMain(main);
                    realmOneForecast.setIcon(icon);
                    realmOneForecast.setHumidity(humidity);
                    realmOneForecast.setWind(wind);
                    realmOneForecast.setDeg(deg);
                    realmOneForecast.setPressure(pressure);

                    realm.copyToRealmOrUpdate(realmOneForecast);
                }
                realm.commitTransaction();


                realm.beginTransaction();
                RealmResults<RealmOneForecast> results = realm.where(RealmOneForecast.class).findAll();
                int extra = results.size() - 40;
                for (int i = 0; i < extra; i++) { results.remove(0); }
                realm.commitTransaction();

            }
            catch (IOException e) { e.printStackTrace(); }
            finally {
                realm.close();
                if (urlConnection != null) { urlConnection.disconnect(); }
                if (reader != null) {
                    try { reader.close(); }
                    catch (final IOException e) { e.printStackTrace();} }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try { jsonToRealm(); }
            catch (JSONException e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }

}