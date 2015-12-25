package com.atrofimenko.weather.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.atrofimenko.weather.R;
import com.atrofimenko.weather.Util.RealmOneForecast;
import com.atrofimenko.weather.activity.MainActivity;

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

/**
 * Created by Anna_T on 09.12.2015.
 */
public class WeatherService extends IntentService {
    private boolean isConnected;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    public WeatherService()
    {
        super("WeatherService");
    }

    @Override
public void onCreate()
    {
        super.onCreate();
        isConnected = isNetworkConnected();
        if (!isConnected) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isConnected) {
            boolean flag = intent.getBooleanExtra(NotificationService.FROM_NOTIFICATION, false);
            if (flag) {
                Intent intentStartApp = new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntentApp = PendingIntent
                        .getActivity(this, 0, intentStartApp, PendingIntent.FLAG_UPDATE_CURRENT);
                builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_stat_sun_512_000000)
                        .setContentTitle(this.getResources().getString(R.string.app_name))
                        .setContentText("Updating ...")
                        .setProgress(0, 0, true)
                        .setContentIntent(pendingIntentApp);
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(NotificationService.NOTIFICATION_ID, builder.build());
            }
            //Realm
            Realm realm = Realm.getInstance(this);
            try {
                JSONArray weatherArray = getJSON();
                jsonToRealm(weatherArray, realm);

                Thread.sleep(1000);
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
            realm.close();

            if (flag) {
                builder.setProgress(0, 0, false)
                        .setContentText("Download complete.");
                mNotificationManager.notify(NotificationService.NOTIFICATION_ID, builder.build());
            }
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private JSONArray getJSON() throws JSONException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr;
        try {
            URL url = new URL(getResources().getString(R.string.openweathermap_url));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setConnectTimeout(20 * 1000);
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
            return weatherArray;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }  // close connection
            if (reader != null) {
                try {
                    reader.close();
                }  // close BufferedReader
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    // JSON -> Realm
    private void jsonToRealm(JSONArray weatherArray, Realm realm) throws JSONException {
        if (weatherArray == null) {
            return;
        }
        realm.beginTransaction();
        for (int i = 0; i < weatherArray.length(); i++) {
            JSONObject oneJSONForecast = weatherArray.getJSONObject(i);
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
}
