package com.zotfeed2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.opengl.Visibility;
import android.os.Binder;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class RadioService extends Service implements OnCompletionListener, OnPreparedListener, OnErrorListener{
    private IBinder mBinder = new LocalBinder();
    private boolean wasPaused = false;
    private boolean playing = false;
    private static final String PLAY_PAUSE = "PLAY_PAUSE";
    private static final int NOTIF_ID = 1;
//    private static final int STOP_CODE = 2;
//    private static final int PAUSE_CODE = 3;
//    private static final int PLAY_CODE = 4;
    private static final String STOP_RADIO = "STOP";

    MediaPlayer mediaPlayer;
    NotificationCompat.Builder builder;
    Constants constants;
    private String url = "http://streamer.kuci.org:8000/high";
    private String currShow = "";
    public RadioService() {
    }

    public class LocalBinder extends Binder {
        public RadioService getService(){
            return RadioService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public void onCreate(){

        super.onCreate();
        constants = (Constants) getApplicationContext();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        IntentFilter filters = new IntentFilter();
        filters.addAction(PLAY_PAUSE);
        filters.addAction(STOP_RADIO);
        registerReceiver(broadcastReceiver, filters);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            try{
                mediaPlayer.reset();
                Toast.makeText(getApplicationContext(), "Connecting to radio stream", Toast.LENGTH_SHORT).show();

                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                currShow = intent.getStringExtra("currShow");
                showNotifcation(currShow);
            }catch (Exception e){
                e.printStackTrace();
            }
        return START_STICKY;
          // prepare async to not block main thread
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        stopForeground(true);
        unregisterReceiver(broadcastReceiver);


    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
            mp.reset();
        else if(what == MediaPlayer.MEDIA_ERROR_TIMED_OUT)
            mp.reset();
        mp.reset();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mp != null) {
            startPlay(mp);
        }
        playing = true;
    }
    public void startPlay(MediaPlayer mp){
        mp.start();
    }
    private void showNotifcation(String currShow){
        //Intent serviceIntent = new Intent(this, RadioService.class);
        //PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_ONE_SHOT);
        builder = new NotificationCompat.Builder(this);
        //Intent for onClick Notification (takes you back to schedules)
        Intent intent = new Intent(this, ViewSchedules.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //intent for stopRadio
        Intent stopRadio = new Intent();
        stopRadio.setAction(STOP_RADIO);
        PendingIntent stopIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopRadio, 0);

        //intent for pause/play Radio
        Intent playPauseRadio = new Intent();
        playPauseRadio.setAction(PLAY_PAUSE);
        PendingIntent playPauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, playPauseRadio, 0);
        builder.setSmallIcon(R.drawable.stopbutton);
        builder.setContentTitle("KUCI Radio Stream");
        builder.setContentText(currShow);
        builder.addAction(R.drawable.stopmini, "Stop", stopIntent);
        builder.addAction(R.drawable.pausemini, "Pause", playPauseIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pIntent);
        startForeground(NOTIF_ID, builder.build());
        //notifStarted = true;
    }
    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(STOP_RADIO)) {
                mediaPlayer.stop();
                builder.mActions.clear();
                stopForeground(true);
                Intent switchIntent = new Intent(context, MainActivity.class);
                switchIntent.putExtra("command", constants.STOP_CODE);
                switchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(switchIntent);
            }
            else if (action.equals(PLAY_PAUSE))
            {
                Intent switchIntent = new Intent(context, MainActivity.class);
                Intent playIntent = new Intent(context, MainActivity.class);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    builder.mActions.clear();
                    builder.addAction(R.drawable.stopmini, "Stop", stopServiceIntent());
                    builder.addAction(R.drawable.playsmall, "Play", resumeServiceIntent());
                    startForeground(NOTIF_ID, builder.build());
                    switchIntent.putExtra("command", constants.PAUSE_CODE);
                    switchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(switchIntent);
                }
                else {
                    mediaPlayer.start();
                    builder.mActions.clear();
                    builder.addAction(R.drawable.stopmini, "Stop", stopServiceIntent());
                    builder.addAction(R.drawable.pausemini, "Pause", resumeServiceIntent());
                    startForeground(NOTIF_ID, builder.build());
                    playIntent.putExtra("command", constants.PLAY_CODE);
                    playIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(playIntent);
                }
            }
        }
    };
    private PendingIntent stopServiceIntent(){
        //intent for stopRadio
        Intent stopRadio = new Intent();
        stopRadio.setAction(STOP_RADIO);
        PendingIntent stopIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopRadio, 0);
        return stopIntent;

    }
    private PendingIntent resumeServiceIntent(){
        //intent for pause/play Radio
        Intent playPauseRadio = new Intent();
        playPauseRadio.setAction(PLAY_PAUSE);
        PendingIntent playPauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, playPauseRadio, 0);
        return playPauseIntent;
    }
}
