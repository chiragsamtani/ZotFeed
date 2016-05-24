package com.zotfeed2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by CHIRAG on 5/19/2016.
 */
public class NotifBroadcast extends BroadcastReceiver {

    private final String PLAY_PAUSE = "PLAY_PAUSE";
    private final String STOP = "STOP";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("da hello");
        if(intent.getAction().equals(PLAY_PAUSE)) {
            Intent switchIntent = new Intent(context, MainActivity.class);
            switchIntent.putExtra("command", "play");
            switchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(switchIntent);

        }else if (intent.getAction().equals(STOP)){
            Intent switchIntent = new Intent(context, MainActivity.class);
            switchIntent.putExtra("command", "stop");
            switchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(switchIntent);
        }
    }
}
