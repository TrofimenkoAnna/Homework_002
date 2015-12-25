package com.atrofimenko.weather.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.atrofimenko.weather.R;
import com.atrofimenko.weather.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service{
    public static final int NOTIFICATION_ID = 0;
    public static final String FROM_NOTIFICATION = "com.atrofimenko.weather.services.STARTED_FROM_NOTIFICATION";

    Timer timer;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    public NotificationService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intentDownload = new Intent(this, WeatherService.class);
        intentDownload.putExtra(FROM_NOTIFICATION, true);
        PendingIntent pendingIntentDownLoad = PendingIntent
                .getService(this, 0, intentDownload, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intentStartApp = new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntentApp = PendingIntent
                .getActivity(this, 0, intentStartApp, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setContentText("New update available")
                .setContentIntent(pendingIntentApp)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_stat_sun_512_000000, "Update", pendingIntentDownLoad);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                mNotificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }, 5*1000, 60*1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY; }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mNotificationManager.cancel(NOTIFICATION_ID);
        timer.cancel();
        super.onDestroy();
    }
}
