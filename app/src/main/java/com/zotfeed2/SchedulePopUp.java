package com.zotfeed2;

import android.content.Intent;
import android.net.Uri;
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
    TextView link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pop_up);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen

//        intent.putExtra("artists", schedule.getHosts());
//        intent.putExtra("description", schedule.getDesc());
//        intent.putExtra("url", schedule.getURL());
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra("showName"));
        artists = (TextView) findViewById(R.id.artists);
        artists.setText("Artists: " + getIntent().getStringExtra("artists"));

        description = (TextView) findViewById(R.id.description);
        description.setText("Description: " + getIntent().getStringExtra("description"));

        link = (TextView) findViewById(R.id.link);
        String linkToArtist = getIntent().getStringExtra("url");
        if(linkToArtist.isEmpty() || linkToArtist.equals(" ")){
            link.setText("Get More Info: " + linkToArtist);
        }else{
            link.setText("");
        }
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getIntent().getStringExtra("url")));
                startActivity(intent);

            }
        });

        // Set title of Detail page
        String title = getIntent().getStringExtra("showName");
        collapsingToolbar.setTitle(title);

    }

}
