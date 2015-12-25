package com.atrofimenko.weather.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.atrofimenko.weather.Util.CallBackInterface;
import com.atrofimenko.weather.activity.MainActivity;
import com.atrofimenko.weather.Util.MyAdapter;
import com.atrofimenko.weather.R;
import com.atrofimenko.weather.Util.RealmOneForecast;
import com.atrofimenko.weather.services.WeatherService;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
                Snackbar.make(view, R.string.no_internet_no_db, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
            }
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
}