package com.zotfeed2;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private boolean playing = false;
    private boolean started = true;
    private FloatingActionButton button;
    private String url = "http://streamer.kuci.org:8000/high";
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button = (FloatingActionButton) findViewById(R.id.fab);
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




}
