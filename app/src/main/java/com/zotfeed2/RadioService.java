package com.zotfeed2;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.provider.SyncStateContract;
import android.widget.Toast;

public class RadioService extends Service implements OnCompletionListener, OnPreparedListener, OnErrorListener{
    private IBinder mBinder = new LocalBinder();
    private boolean wasPaused = false;
    private boolean playing = false;
    MediaPlayer mediaPlayer;
    private String url = "http://streamer.kuci.org:8000/high";
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
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            try{
                if(wasPaused)
                    mediaPlayer.reset();
                Toast.makeText(getApplicationContext(), "Connecting to radio stream", Toast.LENGTH_SHORT).show();

                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
            }catch (Exception e){
                e.printStackTrace();
            }
        return START_STICKY;
          // prepare async to not block main thread
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    protected void pauseStream(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            wasPaused = true;
        }
    }
    protected boolean playing(){
        return playing;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
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
        Toast.makeText(getApplicationContext(), "Stream has started", Toast.LENGTH_LONG).show();
        mp.start();
        playing = true;
    }

}
