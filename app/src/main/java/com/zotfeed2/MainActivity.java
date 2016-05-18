package com.zotfeed2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.DragEvent;
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
    private boolean starting = true;
    private boolean playing = false;
    private ImageButton button;
    private String url = "http://streamer.kuci.org:8000/high";
    private MediaPlayer mediaPlayer;
    private TextView currentShow;
    private Button scheduleButton;
    private ImageButton stopButton;
    private DrawerLayout mDrawerLayout;
    private MyScheduleDB db;
    private RadioService radioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyScheduleDB(this, "Schedules", 1);
        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                        if (menuItem.getTitle().equals("KUCI")) {
                            //do nothing for now
                        } else if (menuItem.getTitle().equals("New University")) {
                            Intent intent = new Intent(getApplicationContext(), NewUniversityActivity.class);
                            startActivity(intent);
                        } else if (menuItem.getTitle().equals("AnteaterTV")) {
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
        try {
            if (!doesDatabaseExist(this)) {
                InputStream file = getResources().getAssets().open("schedule.json");
                readJsonStream(file);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error In Loading Schedules", Toast.LENGTH_SHORT).show();
        }
        button = (ImageButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cannot play it multiple times)
                if(button.getTag() == 1 && !playing){
                    button.setEnabled(false);
                }else{
                    button.setEnabled(true);
                    goToService();
                }

                //      PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ))
                //startRadio() - already invokes mp.start()
                //check if mediaPlayer is Playing
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        button.setImageResource(R.drawable.playbutton);
//                    } else {
//                        mediaPlayer.start();
//                        button.setImageResource(R.drawable.pausebutton);
//                    }

            }
        });

//        stopButton = (ImageButton) findViewById(R.id.stop);
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopRadioService();
//            }
//        });

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("K");
        String formattedTime = sdf.format(now);
        int currTime = Integer.parseInt(formattedTime);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        currentShow = (TextView) findViewById(R.id.showName);
        String showName = db.getScheduleInfo(getDayInString(day), currTime);
        currentShow.setText(showName);


        //view schedules button
        scheduleButton = (Button) findViewById(R.id.schedules);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), ViewSchedules.class);
                startActivity(intent);
            }
        });
    }

    public void startRadio(final String url) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if(what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
                    mp.reset();
                else if(what == MediaPlayer.MEDIA_ERROR_TIMED_OUT)
                    mp.reset();
                startRadio(url);
                return true;
            }
        });
    }
    public void stopRadio(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
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
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                readObject(reader);
            }
            reader.endArray();
        } finally {
            reader.close();
        }
    }

    public void readObject(JsonReader reader) throws IOException {
        Schedule schedule;
        MyScheduleDB db = new MyScheduleDB(this, "Schedules", 1);
        String showName = "";
        String start_time = "";
        String end_time = "";
        String day = "";
        String hosts = "";
        String description = "";
        int startTime = -1;
        int endTime = -1;
        reader.beginObject();

        while (reader.hasNext()) {
            String tag = reader.nextName();
            if (tag.equals("title")) {
                showName = reader.nextString();
            } else if (tag.equals("start_time")) {
                start_time = reader.nextString();
                int index = start_time.indexOf(":");
                start_time = start_time.substring(0, index);
                //System.out.println(start_time);
            } else if (tag.equals("end_time")) {
                end_time = reader.nextString();
                int index = end_time.indexOf(":");
                end_time = end_time.substring(0, index);
                //System.out.println(end_time);
            } else if (tag.equals("day")) {
                day = reader.nextString();
            } else {
                reader.skipValue();
            }
            if (!start_time.isEmpty() || !start_time.equals("")) {
                startTime = Integer.parseInt(start_time);
                System.out.println(startTime);
            }
            if (!end_time.isEmpty() || !end_time.equals("")) {
                endTime = Integer.parseInt(end_time);
            }


//            System.out.println(endTime);
            schedule = new Schedule(showName, day, startTime, endTime);
            if (db.insertSchedule(schedule)) {
                System.out.println("Good");
            }
        }
        reader.endObject();
    }

    private String getDayInString(int day) {
        String dayinStr = "";
        switch (day) {
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
    private void goToService(){
        Intent intent = new Intent();
        intent.setClass(this, RadioService.class);
        if(!playing){
            bindService(intent, connection, getApplicationContext().BIND_AUTO_CREATE);
            button.setImageResource(R.drawable.pausebutton);
            button.setTag(1);
            startService(intent);
            playing = true;
        }
        else{
            button.setTag(2);
            radioService.pauseStream();
            playing = false;
            button.setImageResource(R.drawable.playbutton);
            unbindService(connection);
        }
//        showNotifcation();
    }
    private void stopRadioService(){
        Intent intent = new Intent();
        intent.setClass(this, RadioService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(playing){
            playing = false;
            unbindService(connection);
            button.setImageResource(R.drawable.playbutton);
            stopService(intent);
        }else{
            button.setImageResource(R.drawable.playbutton);
            stopService(intent);
        }

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(playing){
            unbindService(connection);
            playing = false;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            radioService = ((RadioService.LocalBinder) service).getService();
            playing = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playing = false;
        }
    };
    private void  showNotifcation(){
        synchronized (MainActivity.class) {
            Intent serviceIntent = new Intent(this, RadioService.class);
            PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new Notification();
            notification.tickerText = currentShow.getText().toString();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.contentIntent = pIntent;
            notification.notify();
        }

    }
}
