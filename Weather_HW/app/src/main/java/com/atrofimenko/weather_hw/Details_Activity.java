package com.atrofimenko.weather_hw;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class Details_Activity extends AppCompatActivity {
    static final String POSITION_SELECTED = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
          if (myActionBar != null) {
            myActionBar.setDisplayHomeAsUpEnabled(true);
            myActionBar.setTitle("Cherkasy");
            myActionBar.setSubtitle("Weather details");
        }
        int position = getIntent().getExtras().getInt(POSITION_SELECTED);
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            Details_Fragment newFragmentItem = new Details_Fragment();
            newFragmentItem.setItemContent(position);
            manager.beginTransaction()
                    .add(R.id.fragment_item_1, newFragmentItem)
                    .commit();
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
}
