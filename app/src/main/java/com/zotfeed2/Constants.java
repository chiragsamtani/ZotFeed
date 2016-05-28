package com.zotfeed2;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CHIRAG on 5/23/2016.
 */
public class Constants extends Application{
    public static final String PLAY_PAUSE = "PLAY_PAUSE";
    public static final int STOP_CODE = 2;
    public static final int PAUSE_CODE = 3;
    public static final int PLAY_CODE = 4;
    public static final String STOP_RADIO = "STOP";

    @Override
    public void onCreate(){
        super.onCreate();

    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
