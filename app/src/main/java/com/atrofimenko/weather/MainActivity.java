package com.atrofimenko.weather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements CallBackInterface {

    static final String POSITION_SELECTED = "position";
    private int position = -1;
    public static boolean isConnected;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        isConnected = isNetworkConnected();


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setTitle(R.string.app_name);
            myActionBar.setSubtitle(R.string.city);
        }

        manager = getSupportFragmentManager();


        if (savedInstanceState == null) {
            FragmentList mList = new FragmentList();
            manager.beginTransaction()
                    .add(R.id.list_container, mList)
                    .commit();
        }

        if (withDetails()) {
            FragmentDetails mDetails = new FragmentDetails();
            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION_SELECTED);
                if (position != -1) {
                    mDetails.setItemContent(position);
                    manager.beginTransaction()
                            .replace(R.id.details_container, mDetails)
                            .commit();
                }
            }
        } else {

            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION_SELECTED);
            }
        }
    }

    @Override
    public void updateContent(int position) {

        FragmentDetails newFragmentItem = new FragmentDetails();
        newFragmentItem.setItemContent(position);
        this.position = position;
        if (withDetails()) {
            manager.beginTransaction()
                    .replace(R.id.details_container, newFragmentItem)
                    .commit();
        } else {
            Intent intent = new Intent(this, Details_Activity.class);
            intent.putExtra(POSITION_SELECTED, position);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(POSITION_SELECTED, position);
        super.onSaveInstanceState(savedInstanceState);
    }

    private boolean withDetails() {
        boolean with_Details = true;
        with_Details = (findViewById(R.id.details_container) != null);
        return   (with_Details);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
