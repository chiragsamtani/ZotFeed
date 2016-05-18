package com.zotfeed2;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SchedulePopUp extends AppCompatActivity {

    TextView artists;
    TextView description;
    TextView start_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pop_up);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        artists = (TextView) findViewById(R.id.artists);
        artists.setText("Various KUCI Artists");

        description = (TextView) findViewById(R.id.description);
        description.setText("This is a short description of the show");

        start_time = (TextView) findViewById(R.id.start_time);
        start_time.setText(getIntent().getStringExtra("day") + ", " + getIntent().getStringExtra("time"));

        // Set title of Detail page
        String title = getIntent().getStringExtra("showName");
        collapsingToolbar.setTitle(title);

    }

}
