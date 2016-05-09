package com.zotfeed2;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private boolean playing = false;
    private boolean started = true;
    private ImageButton button;
    private String url = "http://streamer.kuci.org:8000/high";
    private MediaPlayer mediaPlayer;
    private TextView currentShow;
    private DrawerLayout mDrawerLayout;
    private MyScheduleDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db =  new MyScheduleDB(this, "Schedules", 1);
        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.menubar);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
         //Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        if(menuItem.getTitle().equals("KUCI")){
                            //do nothing for now
                        }else if(menuItem.getTitle().equals("New University")){
                            Intent intent = new Intent(getApplicationContext(), NewUniversityActivity.class);
                            startActivity(intent);
                        }
                        else if(menuItem.getTitle().equals("AnteaterTV")){
                            Intent intent = new Intent(getApplicationContext(), AnteaterTvActivity.class);
                            startActivity(intent);
                        }
                        menuItem.setChecked(true);

                        // TODO: handle navigation

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        try{
            if(!doesDatabaseExist(this)){
                InputStream file = getResources().getAssets().open("schedule.json");
                readJsonStream(file);
            }
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Occured In Loading Schedules", Toast.LENGTH_SHORT).show();
        }
        button = (ImageButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cannot play it multiple times
                if(!playing){
                    button.setImageResource(R.drawable.pausebutton);
                    if(started)
                        new startRadio().execute(url);
                    else
                    {
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                        }
                    }
                    playing = true;
                }else{
                    //if mediaplayer is currently playing
                    button.setImageResource(R.drawable.playbutton);
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    playing = false;
                }
            }
        });
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("K");
        String formattedTime = sdf.format(now);
        int currTime = Integer.parseInt(formattedTime);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        currentShow = (TextView) findViewById(R.id.showName);
        String showName = db.getScheduleInfo(getDayInString(day), currTime);
        currentShow.setText(showName);
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new SlidingFragment(), "Listen");
        adapter.addFragment(new SlidingFragment(), "Schedule");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




    class startRadio extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try{
                mediaPlayer.setDataSource(params[0]);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "Stream has started", Toast.LENGTH_SHORT).show();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        started = false;
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        Toast.makeText(getApplicationContext(), "Stream is no longer active", Toast.LENGTH_LONG).show();
                        button.setBackgroundResource(R.drawable.playbutton);
                        mediaPlayer = null;
                    }
                });
                mediaPlayer.prepareAsync();
            }catch (IOException e){
                Toast.makeText(getApplicationContext(), "Error Occured In Handling Radio", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
    public void readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try{
            reader.beginArray();
            while(reader.hasNext()){
                readObject(reader);
            }
            reader.endArray();
        }finally{
            reader.close();
        }
    }
    public void readObject(JsonReader reader) throws IOException{
        Schedule schedule;
        MyScheduleDB db = new MyScheduleDB(this, "Schedules", 1);
        String showName = "";
        String start_time = "";
        String end_time = "";
        String day = "";
        int startTime = -1;
        int endTime = -1;
        reader.beginObject();

        while(reader.hasNext()){
            String tag = reader.nextName();
            if(tag.equals("title")){
                showName = reader.nextString();
            }
            else if(tag.equals("start_time")){
                start_time = reader.nextString();
                int index = start_time.indexOf(":");
                start_time = start_time.substring(0, index);
                //System.out.println(start_time);
            }
            else if(tag.equals("end_time")){
                end_time = reader.nextString();
                int index = end_time.indexOf(":");
                end_time = end_time.substring(0, index);
                //System.out.println(end_time);
            }
            else if(tag.equals("day")){
                day = reader.nextString();
            }
            else{
                reader.skipValue();
            }
            if(!start_time.isEmpty() || !start_time.equals("")) {
                startTime = Integer.parseInt(start_time);
                System.out.println(startTime);
            }
            if(!end_time.isEmpty() || !end_time.equals("")) {
                endTime = Integer.parseInt(end_time);
            }


//            System.out.println(endTime);
            schedule = new Schedule(showName, day, startTime, endTime);
            if(db.insertSchedule(schedule)){
                System.out.println("Good");
            }
        }
        reader.endObject();
    }

    private String getDayInString(int day){
        String dayinStr = "";
        switch(day){
            case Calendar.SUNDAY:
                dayinStr = "Sunday";
                break;
            case Calendar.MONDAY:
                dayinStr = "Monday";
                break;
            case Calendar.TUESDAY:
                dayinStr = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayinStr = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayinStr = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayinStr = "Friday";
                break;
            case Calendar.SATURDAY:
                dayinStr = "Saturday";
                break;
        }
        return dayinStr;
    }
    private static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath("Schedules");
        return dbFile.exists();
    }
}
