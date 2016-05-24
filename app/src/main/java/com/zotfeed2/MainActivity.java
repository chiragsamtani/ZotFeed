package com.zotfeed2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private boolean playing = false;
    private static final String PLAY_PAUSE = "PLAY_PAUSE";
    private static final String STOP_RADIO = "STOP";
    private ImageButton button;
    private TextView currentShow;
    private  RadioService radioService;
    private Button scheduleButton;
    private DrawerLayout mDrawerLayout;
    private MyScheduleDB db;
    Constants constants;


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

        constants = (Constants) getApplicationContext();
        //NOW PLAYING FEATURE
        getNowPlaying();

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

        //initialize shared preferences

        button = (ImageButton) findViewById(R.id.fab);
        if(playing){
            button.setImageResource(R.drawable.pausebutton);
        }else{
            button.setImageResource(R.drawable.playbutton);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    new startRadio().execute(url);
                if (!playing) {
                    goToService();
                   // button.setImageResource(R.drawable.pausebutton);
                } else {
                    stopRadioService();
                   // button.setImageResource(R.drawable.playbutton);
                }
            }
        });



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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int command = intent.getIntExtra("command", -1);
        if(command != -1){
            if(command == constants.PLAY_CODE){
                System.out.println("hello");
                button.setImageResource(R.drawable.pausebutton);
            }else if(command == constants.PAUSE_CODE){
                System.out.println("hellqwewqeo");
                button.setImageResource(R.drawable.playbutton);
            }else if(command == constants.STOP_CODE){
                button.setImageResource(R.drawable.playbutton);
            }
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

    @Override
    protected void onDestroy(){
        stopRadioService();
        super.onDestroy();
//        if(notifStarted){
//            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.cancel(NOTIFICATION_ID);
//        }



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
        String genre = "";
        String url = "";
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
            }else if(tag.equals("description")){
                description = reader.nextString();
            }else if(tag.equals("URL")){
                url = reader.nextString();
            }else if(tag.equals("genre")){
                genre = reader.nextString();
            }else if(tag.equals("hosts")){
                hosts = reader.nextString();
            }
            else {
                reader.skipValue();
            }
            if (!start_time.isEmpty() || !start_time.equals("")) {
                startTime = Integer.parseInt(start_time);
                System.out.println(startTime);
            }
            if (!end_time.isEmpty() || !end_time.equals("")) {
                endTime = Integer.parseInt(end_time);
            }

            schedule = new Schedule(showName, day, startTime, endTime, hosts, description, url, genre);
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


    private void  showNotifcation(){
            //Intent serviceIntent = new Intent(this, RadioService.class);
            //PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_ONE_SHOT);

        //Intent for onClick Notification (takes you back to schedules)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, ViewSchedules.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        //intent for stopRadio
        Intent stopRadio = new Intent(this, NotifBroadcast.class);
        stopRadio.setAction(STOP_RADIO);
        PendingIntent stopIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopRadio, 0);
        //intent for pause/play Radio
        Intent playPauseRadio = new Intent(this, NotifBroadcast.class);
        playPauseRadio.setAction(PLAY_PAUSE);
        PendingIntent playPauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, playPauseRadio, 0);


        //initialize
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_layout);
        views.setTextViewText(R.id.notiftext, "KUCI Radio Stream");
        views.setTextViewText(R.id.notifshow, currentShow.getText().toString());
        views.setOnClickPendingIntent(R.id.notifstop, stopIntent);
        views.setOnClickPendingIntent(R.id.notifplay, playPauseIntent);
        builder.setContent(views);
        builder.setSmallIcon(R.drawable.stopbutton);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
        //notifStarted = true;
    }
    private void getNowPlaying() {
        if(!doesDatabaseExist(getApplicationContext())) {
            if (isNetworkAvailable()) {
                //final Map<String, String> params = new HashMap<String, String>();
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.kuci.org/schedule.json";
                StringRequest req = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (!response.isEmpty()) {
                                    try {
                                        InputStream in = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
                                        readJsonStream(in);
                                        System.out.print(response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {

                };
                queue.add(req);
            } else {
                try {
                    InputStream file = getResources().getAssets().open("schedule.json");
                    readJsonStream(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void goToService(){
        playing = true;
        Intent intent = new Intent();
        intent.setClass(this, RadioService.class);
       // intent.putExtra("playing", playing);
        intent.putExtra("currShow", currentShow.getText().toString());
        bindService(intent, connection, getApplicationContext().BIND_AUTO_CREATE);
        startService(intent);
        button.setImageResource(R.drawable.pausebutton);

//        showNotifcation();
    }
    private void stopRadioService(){
        playing = false;
        Intent intent = new Intent();
        intent.setClass(this, RadioService.class);
       // intent.putExtra("playing", playing);
        button.setImageResource(R.drawable.playbutton);
        unbindService(connection);
        stopService(intent);


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




//    class startRadio extends AsyncTask<String, Void, Boolean> {
//        ProgressDialog dialog;
//        @Override
//        protected void onPreExecute() {
//            dialog = new ProgressDialog(MainActivity.this);
//            dialog.setMessage("Connecting to RSS Feed");
//            dialog.show();
//            button.setClickable(false);
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mediaPlayer.setDataSource(params[0]);
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        dialog.dismiss();
//                        button.setClickable(true);
//                        mp.start();
//                        Toast.makeText(getApplicationContext(), "Stream has started", Toast.LENGTH_SHORT).show();
//                        showNotifcation();
//                    }
//                });
//                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
//                            mp.reset();
//                        } else if (what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
//                            mp.reset();
//                        }
//                        return true;
//                    }
//                });
//            } catch (IOException e) {
//                Toast.makeText(getApplicationContext(), "Error Occured In Handling Radio", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            return true;
//        }
//
//
//    }
//    public class NotifBroadcast extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            System.out
////            if(intent.getAction().equals(PLAY_PAUSE)){
////                if(mediaPlayer.isPlaying()){
////                    mediaPlayer.pause();
////                }else{
////                    mediaPlayer.start();
////                }
////            }else if(intent.getAction().equals(STOP_RADIO)){
////                if(mediaPlayer.isPlaying()){
////                    mediaPlayer.stop();
////                }
////            }
//        }
//    }

}
