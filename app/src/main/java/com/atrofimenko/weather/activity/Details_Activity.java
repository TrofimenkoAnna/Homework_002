package com.atrofimenko.weather.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.atrofimenko.weather.R;
import com.atrofimenko.weather.fragments.FragmentDetails;

public class Details_Activity extends AppCompatActivity {

    static final String POSITION_SELECTED = "position";
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_hw06_toolbar_b);
        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setDisplayHomeAsUpEnabled(true);
            myActionBar.setTitle(R.string.city);
            myActionBar.setSubtitle(R.string.weather_details);
        }

        if (savedInstanceState == null) {
            position = getIntent().getExtras().getInt(POSITION_SELECTED);
            FragmentManager manager = getSupportFragmentManager();
            FragmentDetails newFragmentItem = new FragmentDetails();
            newFragmentItem.setItemContent(position);
            manager.beginTransaction()
                    .add(R.id.hw06_fragment_item_b, newFragmentItem, "details")
                    .commit();
        } else {
            position = savedInstanceState.getInt(POSITION_SELECTED);
            FragmentDetails oldFragmentItem =  (FragmentDetails) getSupportFragmentManager().findFragmentByTag("details");
            oldFragmentItem.setItemContent(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(POSITION_SELECTED, position);
        super.onSaveInstanceState(savedInstanceState);
    }
}
